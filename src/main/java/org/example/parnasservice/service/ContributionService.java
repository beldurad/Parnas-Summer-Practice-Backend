package org.example.parnasservice.service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.parnasservice.dto.request.ConfirmContributionRequest;
import org.example.parnasservice.dto.request.PrepareContributionRequest;
import org.example.parnasservice.dto.response.ContributionAcceptedResponse;
import org.example.parnasservice.dto.response.ContributionListResponse;
import org.example.parnasservice.dto.response.PreparedContributionResponse;
import org.example.parnasservice.entity.Campaign;
import org.example.parnasservice.entity.Contribution;
import org.example.parnasservice.entity.User;
import org.example.parnasservice.entity.enums.CampaignStatus;
import org.example.parnasservice.entity.enums.ContributionStatus;
import org.example.parnasservice.entity.enums.TransactionType;
import org.example.parnasservice.exception.ConflictException;
import org.example.parnasservice.exception.ResourceNotFoundException;
import org.example.parnasservice.mapper.EntityMapper;
import org.example.parnasservice.repository.BlockchainTransactionRepository;
import org.example.parnasservice.repository.CampaignRepository;
import org.example.parnasservice.repository.ContributionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionService {

    private final CampaignRepository campaignRepository;
    private final ContributionRepository contributionRepository;
    private final BlockchainTransactionRepository blockchainTransactionRepository;
    private final EntityMapper entityMapper;
    private final BlockchainService blockchainService;

    @Transactional
    public PreparedContributionResponse prepareContribution(UUID campaignId,
                                                            PrepareContributionRequest request,
                                                            User currentUser) {
        requireAuthenticated(currentUser);
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException("Кампания не найдена"));

        requireOpenCampaign(campaign);
        if (campaign.getChainId() != request.getChainId()) {
            throw new IllegalArgumentException("Chain id does not match campaign chain id.");
        }

        BigInteger raised = new BigInteger(campaign.getRaisedAmount());
        BigInteger target = new BigInteger(campaign.getTargetAmount());
        BigInteger amount = new BigInteger(request.getAmountRaw());
        BigInteger remaining = target.subtract(raised);
        if (amount.compareTo(remaining) > 0) {
            throw new ConflictException("TARGET_EXCEEDED", "Contribution exceeds campaign remaining goal.");
        }

        Contribution contribution = new Contribution();
        contribution.setId(UUID.randomUUID());
        contribution.setCampaign(campaign);
        contribution.setContributorUser(currentUser);
        contribution.setContributorWallet(currentUser.getWalletAddress());
        contribution.setAmount(request.getAmountRaw());
        contribution.setStatus(ContributionStatus.PREPARED);
        contribution.setCreatedAt(Instant.now());
        contribution = contributionRepository.save(contribution);
        log.info("Contribution prepared: contributionId={} campaignId={} userId={} wallet={} amount={}",
            contribution.getId(), campaignId, currentUser.getId(), currentUser.getWalletAddress(), contribution.getAmount());

        var tx = blockchainService.contributionTransaction(
            currentUser.getWalletAddress(),
            campaign.getContractAddress(),
            contribution.getAmount()
        );

        return new PreparedContributionResponse(
            entityMapper.toContributionResponse(contribution),
            tx,
            entityMapper.toTokenAmount(remaining.toString())
        );
    }

    @Transactional
    public ContributionAcceptedResponse confirmContribution(UUID campaignId,
                                                            ConfirmContributionRequest request,
                                                            User currentUser) {
        requireAuthenticated(currentUser);
        log.info("Contribution confirmation requested: contributionId={} campaignId={} userId={} txHash={}",
            request.getContributionId(), campaignId, currentUser.getId(), request.getTransactionHash());
        Contribution contribution = contributionRepository.findById(request.getContributionId())
            .orElseThrow(() -> new ResourceNotFoundException("Взнос не найден"));
        if (!contribution.getCampaign().getId().equals(campaignId)) {
            throw new ResourceNotFoundException("Взнос не найден для данной кампании");
        }
        if (!contribution.getContributorUser().getId().equals(currentUser.getId())) {
            throw new ConflictException("CONTRIBUTION_OWNER_MISMATCH", "Only contribution owner can confirm it.");
        }
        if (contribution.getStatus() == ContributionStatus.CONFIRMED) {
            throw new ConflictException("CONTRIBUTION_ALREADY_CONFIRMED", "Contribution is already confirmed.");
        }

        Campaign campaign = contribution.getCampaign();
        var receipt = blockchainService.requireSuccessfulReceipt(request.getTransactionHash());
        var tx = blockchainService.requireTransaction(request.getTransactionHash());
        requireTxFrom(tx.getFrom(), currentUser.getWalletAddress());
        requireTxTarget(tx.getTo(), campaign.getContractAddress());
        requireTxValue(tx.getValue(), contribution.getAmount());
        blockchainService.requireContributionState(
            campaign.getContractAddress(),
            currentUser.getWalletAddress(),
            contribution.getAmount()
        );

        contribution.setStatus(ContributionStatus.CONFIRMED);
        contribution.setTransactionHash(request.getTransactionHash());
        contribution.setConfirmedAt(Instant.now());
        contributionRepository.save(contribution);
        log.info("Contribution confirmed: contributionId={} campaignId={} amount={} txHash={}",
            contribution.getId(), campaignId, contribution.getAmount(), request.getTransactionHash());

        BigInteger raised = new BigInteger(campaign.getRaisedAmount()).add(new BigInteger(contribution.getAmount()));
        campaign.setRaisedAmount(raised.toString());
        if (raised.compareTo(new BigInteger(campaign.getTargetAmount())) >= 0) {
            campaign.setStatus(CampaignStatus.CLOSED_GOAL_REACHED);
            log.info("Campaign goal reached: campaignId={} raised={} target={}",
                campaign.getId(), raised, campaign.getTargetAmount());
        }
        campaignRepository.save(campaign);

        blockchainTransactionRepository.save(blockchainService.toBlockchainTransaction(
            tx, receipt, TransactionType.CONTRIBUTION, contribution.getAmount()));

        return new ContributionAcceptedResponse(
            entityMapper.toContributionResponse(contribution),
            "/api/v1/campaigns/" + campaignId + "/contributions"
        );
    }

    @Transactional(readOnly = true)
    public ContributionListResponse getCampaignContributions(UUID campaignId, int page, int pageSize) {
        campaignRepository.findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException("Кампания не найдена"));

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Contribution> contributions = contributionRepository.findByCampaignId(campaignId, pageable);

        return new ContributionListResponse(
            contributions.getContent().stream()
                .map(entityMapper::toContributionResponse)
                .collect(Collectors.toList()),
            entityMapper.toPaginationMeta(contributions)
        );
    }

    private static void requireOpenCampaign(Campaign campaign) {
        if (campaign.getStatus() != CampaignStatus.OPEN || campaign.getContractAddress() == null) {
            throw new ConflictException("CAMPAIGN_NOT_OPEN", "Campaign is not open for contributions.");
        }
        if (campaign.getDeadline().isBefore(Instant.now())) {
            throw new ConflictException("CAMPAIGN_DEADLINE_EXPIRED", "Campaign deadline has expired.");
        }
    }

    private static void requireTxTarget(String actual, String expected) {
        if (actual == null || !actual.equalsIgnoreCase(expected)) {
            throw new ConflictException("TRANSACTION_TARGET_MISMATCH", "Transaction target contract does not match campaign.");
        }
    }

    private static void requireTxFrom(String actual, String expected) {
        if (actual == null || !actual.equalsIgnoreCase(expected)) {
            throw new ConflictException("TRANSACTION_SENDER_MISMATCH", "Transaction sender does not match current user.");
        }
    }

    private static void requireTxValue(BigInteger actual, String expected) {
        if (!actual.equals(new BigInteger(expected))) {
            throw new ConflictException("TRANSACTION_VALUE_MISMATCH", "Transaction value does not match prepared amount.");
        }
    }

    private static void requireAuthenticated(User user) {
        if (user == null) {
            throw new org.example.parnasservice.exception.ForbiddenException("Authentication is required.");
        }
    }
}
