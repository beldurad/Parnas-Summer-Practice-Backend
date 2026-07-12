package org.example.parnasservice.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.parnasservice.dto.request.ConfirmPayoutRequest;
import org.example.parnasservice.dto.request.PreparePayoutRequest;
import org.example.parnasservice.dto.response.PayoutAcceptedResponse;
import org.example.parnasservice.dto.response.PayoutDetail;
import org.example.parnasservice.dto.response.PayoutDistributionListResponse;
import org.example.parnasservice.dto.response.PayoutDistributionPreview;
import org.example.parnasservice.dto.response.PayoutDistributionPreviewItem;
import org.example.parnasservice.dto.response.PayoutDistributionResponse;
import org.example.parnasservice.dto.response.PayoutListResponse;
import org.example.parnasservice.dto.response.PayoutSummary;
import org.example.parnasservice.dto.response.PreparedPayoutResponse;
import org.example.parnasservice.entity.Campaign;
import org.example.parnasservice.entity.Contribution;
import org.example.parnasservice.entity.Payout;
import org.example.parnasservice.entity.PayoutDistribution;
import org.example.parnasservice.entity.User;
import org.example.parnasservice.entity.enums.CampaignStatus;
import org.example.parnasservice.entity.enums.ContributionStatus;
import org.example.parnasservice.entity.enums.PayoutStatus;
import org.example.parnasservice.entity.enums.TransactionType;
import org.example.parnasservice.entity.enums.TransferStatus;
import org.example.parnasservice.exception.ConflictException;
import org.example.parnasservice.exception.ForbiddenException;
import org.example.parnasservice.exception.ResourceNotFoundException;
import org.example.parnasservice.mapper.EntityMapper;
import org.example.parnasservice.repository.BlockchainTransactionRepository;
import org.example.parnasservice.repository.CampaignRepository;
import org.example.parnasservice.repository.ContributionRepository;
import org.example.parnasservice.repository.PayoutDistributionRepository;
import org.example.parnasservice.repository.PayoutRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutService {

    private final CampaignRepository campaignRepository;
    private final PayoutRepository payoutRepository;
    private final PayoutDistributionRepository distributionRepository;
    private final ContributionRepository contributionRepository;
    private final BlockchainTransactionRepository blockchainTransactionRepository;
    private final EntityMapper entityMapper;
    private final BlockchainService blockchainService;

    @Transactional
    public PreparedPayoutResponse preparePayout(UUID campaignId,
                                                PreparePayoutRequest request,
                                                User currentUser) {
        requireAuthenticated(currentUser);
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException("Кампания не найдена"));
        requirePayoutAllowed(campaign, currentUser);
        if (campaign.getChainId() != request.getChainId()) {
            throw new IllegalArgumentException("Chain id does not match campaign chain id.");
        }
        if (payoutRepository.existsByCampaignIdAndStatus(campaignId, PayoutStatus.CONFIRMED)) {
            throw new ConflictException("PAYOUT_ALREADY_EXECUTED", "Confirmed payout already exists.");
        }

        List<DistributionCalc> calc = calculateDistributions(campaignId, request.getProfitAmountRaw());
        if (calc.isEmpty()) {
            throw new ConflictException("NO_CONFIRMED_CONTRIBUTORS", "No confirmed contributors for payout.");
        }

        Payout payout = new Payout();
        payout.setId(UUID.randomUUID());
        payout.setCampaign(campaign);
        payout.setProfitAmount(request.getProfitAmountRaw());
        payout.setStatus(PayoutStatus.PREPARED);
        payout.setCreatedAt(Instant.now());
        payout = payoutRepository.save(payout);
        log.info("Payout prepared: payoutId={} campaignId={} userId={} profitAmount={} recipients={}",
            payout.getId(), campaignId, currentUser.getId(), payout.getProfitAmount(), calc.size());

        for (DistributionCalc item : calc) {
            PayoutDistribution distribution = new PayoutDistribution();
            distribution.setId(UUID.randomUUID());
            distribution.setPayout(payout);
            distribution.setWalletAddress(item.walletAddress());
            distribution.setContributionAmount(item.contributionAmount().toString());
            distribution.setSharePercent(item.sharePercent());
            distribution.setPayoutAmount(item.payoutAmount().toString());
            distribution.setTransferStatus(TransferStatus.PENDING);
            distributionRepository.save(distribution);
        }

        var tx = blockchainService.payoutTransaction(
            currentUser.getWalletAddress(),
            campaign.getContractAddress(),
            payout.getProfitAmount()
        );

        return new PreparedPayoutResponse(
            entityMapper.toPayoutSummary(payout),
            toPreview(calc, request.getProfitAmountRaw()),
            tx
        );
    }

    @Transactional
    public PayoutAcceptedResponse confirmPayout(UUID campaignId,
                                                ConfirmPayoutRequest request,
                                                User currentUser) {
        requireAuthenticated(currentUser);
        log.info("Payout confirmation requested: payoutId={} campaignId={} userId={} txHash={}",
            request.getPayoutId(), campaignId, currentUser.getId(), request.getTransactionHash());
        Payout payout = payoutRepository.findById(request.getPayoutId())
            .orElseThrow(() -> new ResourceNotFoundException("Выплата не найдена"));
        if (!payout.getCampaign().getId().equals(campaignId)) {
            throw new ResourceNotFoundException("Выплата не найдена для данной кампании");
        }
        Campaign campaign = payout.getCampaign();
        requirePayoutAllowed(campaign, currentUser);
        if (payout.getStatus() == PayoutStatus.CONFIRMED) {
            throw new ConflictException("PAYOUT_ALREADY_EXECUTED", "Payout is already confirmed.");
        }

        var receipt = blockchainService.requireSuccessfulReceipt(request.getTransactionHash());
        var tx = blockchainService.requireTransaction(request.getTransactionHash());
        requireTxFrom(tx.getFrom(), currentUser.getWalletAddress());
        requireTxTarget(tx.getTo(), campaign.getContractAddress());
        requireTxValue(tx.getValue(), payout.getProfitAmount());
        blockchainService.requireProfitDistributedState(campaign.getContractAddress(), payout.getProfitAmount());

        payout.setStatus(PayoutStatus.CONFIRMED);
        payout.setTransactionHash(request.getTransactionHash());
        payout.setConfirmedAt(Instant.now());
        payoutRepository.save(payout);
        log.info("Payout confirmed: payoutId={} campaignId={} profitAmount={} txHash={}",
            payout.getId(), campaignId, payout.getProfitAmount(), request.getTransactionHash());

        distributionRepository.findByPayoutId(payout.getId(), Pageable.unpaged())
            .forEach(distribution -> {
                distribution.setTransferStatus(TransferStatus.CONFIRMED);
                distribution.setTransactionHash(request.getTransactionHash());
                distributionRepository.save(distribution);
            });

        blockchainTransactionRepository.save(blockchainService.toBlockchainTransaction(
            tx, receipt, TransactionType.PAYOUT, payout.getProfitAmount()));

        return new PayoutAcceptedResponse(
            entityMapper.toPayoutSummary(payout),
            "/api/v1/campaigns/" + campaignId + "/payouts/" + payout.getId()
        );
    }

    @Transactional(readOnly = true)
    public PayoutListResponse getCampaignPayouts(UUID campaignId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Payout> payouts = payoutRepository.findByCampaignId(campaignId, pageable);

        List<PayoutSummary> items = payouts.getContent().stream()
            .map(entityMapper::toPayoutSummary)
            .collect(Collectors.toList());

        return new PayoutListResponse(items, entityMapper.toPaginationMeta(payouts));
    }

    @Transactional(readOnly = true)
    public PayoutDetail getPayout(UUID campaignId, UUID payoutId) {
        Payout payout = payoutRepository.findById(payoutId)
            .orElseThrow(() -> new ResourceNotFoundException("Выплата не найдена"));

        if (!payout.getCampaign().getId().equals(campaignId)) {
            throw new ResourceNotFoundException("Выплата не найдена для данной кампании");
        }

        PayoutDetail detail = new PayoutDetail();
        detail.setPayout(entityMapper.toPayoutSummary(payout));
        Campaign campaign = payout.getCampaign();
        int contributorsCount = contributionRepository.countByCampaignIdAndStatus(campaign.getId(), ContributionStatus.CONFIRMED);
        List<Payout> campaignPayouts = payoutRepository.findByCampaignId(campaign.getId());
        String totalProfitDistributed = campaignPayouts.stream()
            .filter(p -> p.getStatus() == PayoutStatus.CONFIRMED)
            .map(Payout::getProfitAmount)
            .reduce((a, b) -> new BigInteger(a).add(new BigInteger(b)).toString())
            .orElse("0");
        detail.setCampaign(entityMapper.toCampaignSummary(
            campaign,
            contributorsCount,
            campaignPayouts.size(),
            totalProfitDistributed
        ));
        detail.setDistributedAmount(entityMapper.toTokenAmount(payout.getProfitAmount()));
        detail.setRoundingRemainder(entityMapper.toTokenAmount("0"));
        if (payout.getTransactionHash() != null) {
            blockchainTransactionRepository.findByHash(payout.getTransactionHash())
                .map(entityMapper::toBlockchainTransactionResponse)
                .ifPresent(detail::setTransaction);
        }
        return detail;
    }

    @Transactional(readOnly = true)
    public PayoutDistributionListResponse getPayoutDistributions(UUID campaignId, UUID payoutId,
                                                                  int page, int pageSize) {
        Payout payout = payoutRepository.findById(payoutId)
            .orElseThrow(() -> new ResourceNotFoundException("Выплата не найдена"));
        if (!payout.getCampaign().getId().equals(campaignId)) {
            throw new ResourceNotFoundException("Выплата не найдена для данной кампании");
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<PayoutDistribution> distributions = distributionRepository.findByPayoutId(payoutId, pageable);

        List<PayoutDistributionResponse> items = distributions.getContent().stream()
            .map(entityMapper::toPayoutDistributionResponse)
            .collect(Collectors.toList());

        return new PayoutDistributionListResponse(items, entityMapper.toPaginationMeta(distributions));
    }

    private void requirePayoutAllowed(Campaign campaign, User currentUser) {
        if (!campaign.getCreator().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Только создатель кампании может подготовить выплату.");
        }
        if (campaign.getStatus() != CampaignStatus.CLOSED_GOAL_REACHED || campaign.getContractAddress() == null) {
            throw new ConflictException("PAYOUT_NOT_ALLOWED", "Payout is available only after campaign goal is reached.");
        }
    }

    private List<DistributionCalc> calculateDistributions(UUID campaignId, String profitAmountRaw) {
        BigInteger profit = new BigInteger(profitAmountRaw);
        Map<String, BigInteger> byWallet = contributionRepository
            .findByCampaignIdAndStatus(campaignId, ContributionStatus.CONFIRMED)
            .stream()
            .collect(Collectors.groupingBy(
                Contribution::getContributorWallet,
                Collectors.reducing(
                    BigInteger.ZERO,
                    c -> new BigInteger(c.getAmount()),
                    BigInteger::add
                )
            ));
        BigInteger total = byWallet.values().stream().reduce(BigInteger.ZERO, BigInteger::add);
        if (total.equals(BigInteger.ZERO)) {
            return List.of();
        }

        List<DistributionCalc> result = byWallet.entrySet().stream()
            .map(entry -> {
                BigInteger payout = profit.multiply(entry.getValue()).divide(total);
                double share = new BigDecimal(entry.getValue())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(new BigDecimal(total), 6, RoundingMode.HALF_UP)
                    .doubleValue();
                return new DistributionCalc(entry.getKey(), entry.getValue(), share, payout);
            })
            .collect(Collectors.toList());

        BigInteger distributed = result.stream()
            .map(DistributionCalc::payoutAmount)
            .reduce(BigInteger.ZERO, BigInteger::add);
        BigInteger remainder = profit.subtract(distributed);
        if (remainder.signum() > 0) {
            DistributionCalc winner = result.stream()
                .max(Comparator.comparing(DistributionCalc::contributionAmount)
                    .thenComparing(DistributionCalc::walletAddress, Comparator.reverseOrder()))
                .orElseThrow();
            result = result.stream()
                .map(item -> item.walletAddress().equalsIgnoreCase(winner.walletAddress())
                    ? new DistributionCalc(
                        item.walletAddress(),
                        item.contributionAmount(),
                        item.sharePercent(),
                        item.payoutAmount().add(remainder))
                    : item)
                .collect(Collectors.toList());
        }
        return result;
    }

    private PayoutDistributionPreview toPreview(List<DistributionCalc> calc, String profitAmountRaw) {
        List<PayoutDistributionPreviewItem> items = calc.stream()
            .limit(20)
            .map(item -> new PayoutDistributionPreviewItem(
                item.walletAddress(),
                entityMapper.toTokenAmount(item.contributionAmount().toString()),
                item.sharePercent(),
                entityMapper.toTokenAmount(item.payoutAmount().toString())
            ))
            .collect(Collectors.toList());
        return new PayoutDistributionPreview(
            calc.size(),
            "floor(profit * contribution / totalRaised), remainder to largest contribution then lexicographically smaller wallet",
            entityMapper.toTokenAmount(profitAmountRaw),
            items
        );
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
            throw new ForbiddenException("Authentication is required.");
        }
    }

    private record DistributionCalc(
        String walletAddress,
        BigInteger contributionAmount,
        double sharePercent,
        BigInteger payoutAmount
    ) {
    }
}
