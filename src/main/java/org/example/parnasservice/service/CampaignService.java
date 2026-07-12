package org.example.parnasservice.service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.parnasservice.dto.request.CreateCampaignRequest;
import org.example.parnasservice.dto.response.BlockchainTransactionResponse;
import org.example.parnasservice.dto.response.CampaignActionAvailability;
import org.example.parnasservice.dto.response.CampaignDetail;
import org.example.parnasservice.dto.response.CampaignListResponse;
import org.example.parnasservice.dto.response.CampaignListItem;
import org.example.parnasservice.dto.response.CampaignManagementView;
import org.example.parnasservice.dto.response.ContributionResponse;
import org.example.parnasservice.dto.response.ContributorListResponse;
import org.example.parnasservice.dto.response.ContributorResponse;
import org.example.parnasservice.dto.response.FinalizeExpiredCampaignsResponse;
import org.example.parnasservice.dto.response.PaginationMeta;
import org.example.parnasservice.dto.response.PayoutSummary;
import org.example.parnasservice.entity.Campaign;
import org.example.parnasservice.entity.Contribution;
import org.example.parnasservice.entity.Payout;
import org.example.parnasservice.entity.User;
import org.example.parnasservice.entity.enums.CampaignStatus;
import org.example.parnasservice.entity.enums.ContributionStatus;
import org.example.parnasservice.entity.enums.PayoutStatus;
import org.example.parnasservice.exception.ForbiddenException;
import org.example.parnasservice.exception.ResourceNotFoundException;
import org.example.parnasservice.mapper.EntityMapper;
import org.example.parnasservice.repository.CampaignRepository;
import org.example.parnasservice.repository.ContributionRepository;
import org.example.parnasservice.repository.PayoutDistributionRepository;
import org.example.parnasservice.repository.PayoutRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ContributionRepository contributionRepository;
    private final PayoutRepository payoutRepository;
    private final PayoutDistributionRepository payoutDistributionRepository;
    private final EntityMapper entityMapper;

    @Transactional(readOnly = true)
    public CampaignListResponse listCampaigns(String query, List<CampaignStatus> statuses,
                                               UUID creatorId, String sort, int page, int pageSize) {
        Sort sortBy = parseSort(sort);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sortBy);
        Page<Campaign> campaignPage;

        if (query != null || (statuses != null && !statuses.isEmpty()) || creatorId != null) {
            campaignPage = campaignRepository.searchCampaigns(query, statuses, creatorId, pageable);
        } else {
            campaignPage = campaignRepository.findAll(pageable);
        }

        List<CampaignListItem> items = campaignPage.getContent().stream()
            .map(entityMapper::toCampaignListItem)
            .collect(Collectors.toList());

        return new CampaignListResponse(items, entityMapper.toPaginationMeta(campaignPage));
    }

    @Transactional
    public Campaign createCampaign(CreateCampaignRequest request, User creator) {
        if (request.getDeadline().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Дедлайн должен быть в будущем.");
        }

        Campaign campaign = new Campaign();
        campaign.setId(UUID.randomUUID());
        campaign.setTitle(request.getTitle());
        campaign.setDescription(request.getDescription());
        campaign.setShortDescription(request.getDescription().length() > 300
            ? request.getDescription().substring(0, 300) : request.getDescription());
        campaign.setCreator(creator);
        campaign.setTargetAmount(request.getTargetAmountRaw());
        campaign.setRaisedAmount("0");
        campaign.setStatus(CampaignStatus.OPEN);
        campaign.setDeadline(request.getDeadline());
        campaign.setChainId(request.getChainId());
        campaign.setCreatedAt(Instant.now());
        return campaignRepository.save(campaign);
    }

    @Transactional(readOnly = true)
    public CampaignDetail getCampaign(UUID campaignId, boolean includeRecentTransactions) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException("Кампания не найдена"));

        int contributorsCount = contributionRepository.countByCampaignIdAndStatus(campaignId, ContributionStatus.CONFIRMED);
        List<Payout> payouts = payoutRepository.findByCampaignId(campaignId);
        int payoutsCount = payouts.size();
        String totalProfitDistributed = payouts.stream()
            .filter(p -> p.getStatus() == PayoutStatus.CONFIRMED)
            .map(Payout::getProfitAmount)
            .reduce((a, b) -> new BigInteger(a).add(new BigInteger(b)).toString())
            .orElse("0");

        List<PayoutSummary> payoutSummaries = payouts.stream()
            .map(entityMapper::toPayoutSummary)
            .collect(Collectors.toList());

        List<BlockchainTransactionResponse> transactions = Collections.emptyList();

        return entityMapper.toCampaignDetail(campaign, contributorsCount, payoutsCount,
            totalProfitDistributed, payoutSummaries, transactions);
    }

    @Transactional(readOnly = true)
    public CampaignManagementView getManagement(UUID campaignId, User currentUser) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException("Кампания не найдена"));

        if (!campaign.getCreator().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Только создатель кампании может просматривать управление.");
        }

        int contributorsCount = contributionRepository.countByCampaignIdAndStatus(campaignId, ContributionStatus.CONFIRMED);
        List<Payout> payouts = payoutRepository.findByCampaignId(campaignId);
        int payoutsCount = payouts.size();
        String totalProfitDistributed = payouts.stream()
            .filter(p -> p.getStatus() == PayoutStatus.CONFIRMED)
            .map(Payout::getProfitAmount)
            .reduce((a, b) -> new BigInteger(a).add(new BigInteger(b)).toString())
            .orElse("0");

        List<PayoutSummary> payoutSummaries = payouts.stream()
            .map(entityMapper::toPayoutSummary)
            .collect(Collectors.toList());

        List<ContributionResponse> recentContributions = contributionRepository
            .findByCampaignId(campaignId).stream()
            .map(entityMapper::toContributionResponse)
            .collect(Collectors.toList());

        CampaignDetail detail = entityMapper.toCampaignDetail(campaign, contributorsCount, payoutsCount,
            totalProfitDistributed, payoutSummaries, Collections.emptyList());

        CampaignActionAvailability actions = computeActions(campaign);

        CampaignManagementView view = new CampaignManagementView();
        view.setCampaign(detail);
        view.setActions(actions);
        view.setRecentContributions(recentContributions);
        view.setPayouts(payoutSummaries);
        return view;
    }

    @Transactional
    public FinalizeExpiredCampaignsResponse finalizeExpired(Integer batchSize, boolean dryRun) {
        List<Campaign> expired = campaignRepository.findExpiredOpenCampaigns(Instant.now());
        int candidates = expired.size();

        if (!dryRun) {
            int limit = batchSize != null ? Math.min(batchSize, expired.size()) : expired.size();
            for (int i = 0; i < limit; i++) {
                Campaign c = expired.get(i);
                c.setStatus(CampaignStatus.FAILED_DEADLINE);
                campaignRepository.save(c);
            }
        }

        return new FinalizeExpiredCampaignsResponse(UUID.randomUUID(), "QUEUED", candidates);
    }

    @Transactional(readOnly = true)
    public ContributorListResponse getContributors(UUID campaignId, int page, int pageSize) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException("Кампания не найдена"));

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Contribution> contributionPage = contributionRepository.findByCampaignId(campaignId, pageable);

        List<ContributorResponse> items = contributionPage.getContent().stream()
            .collect(Collectors.groupingBy(
                Contribution::getContributorWallet,
                Collectors.summingLong(c -> Long.parseLong(c.getAmount()))
            ))
            .entrySet().stream()
            .map(e -> {
                double share = new BigInteger(String.valueOf(e.getValue()))
                    .multiply(BigInteger.valueOf(10000))
                    .divide(new BigInteger(campaign.getTargetAmount()))
                    .doubleValue() / 100.0;
                return entityMapper.toContributorResponse(e.getKey(),
                    String.valueOf(e.getValue()), share, 1);
            })
            .collect(Collectors.toList());

        PaginationMeta meta = new PaginationMeta(page, pageSize, items.size(),
            (int) Math.ceil((double) items.size() / pageSize));
        return new ContributorListResponse(items, meta);
    }

    private static Sort parseSort(String sort) {
        if (sort == null) return Sort.by(Sort.Direction.DESC, "createdAt");
        return switch (sort) {
            case "POPULARITY_DESC" -> Sort.by(Sort.Direction.DESC, "raisedAmount");
            case "CREATED_AT_ASC" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "DEADLINE_ASC" -> Sort.by(Sort.Direction.ASC, "deadline");
            case "DEADLINE_DESC" -> Sort.by(Sort.Direction.DESC, "deadline");
            case "TARGET_AMOUNT_ASC" -> Sort.by(Sort.Direction.ASC, "targetAmount");
            case "TARGET_AMOUNT_DESC" -> Sort.by(Sort.Direction.DESC, "targetAmount");
            case "PROGRESS_DESC" -> Sort.by(Sort.Direction.DESC, "raisedAmount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    private CampaignActionAvailability computeActions(Campaign campaign) {
        CampaignActionAvailability a = new CampaignActionAvailability();
        a.setCanViewManagement(true);

        if (campaign.getStatus() == CampaignStatus.OPEN) {
            a.setCanContribute(true);
            a.setCanPreparePayout(false);
            a.setCanFinalize(campaign.getDeadline().isBefore(Instant.now()));
        } else if (campaign.getStatus() == CampaignStatus.CLOSED_GOAL_REACHED) {
            a.setCanContribute(false);
            a.setContributeBlockedReason("Цель достигнута, взносы закрыты.");
            boolean payoutExists = payoutRepository.existsByCampaignIdAndStatus(campaign.getId(), PayoutStatus.CONFIRMED);
            a.setCanPreparePayout(!payoutExists);
            a.setCanFinalize(false);
        } else if (campaign.getStatus() == CampaignStatus.FAILED_DEADLINE) {
            a.setCanContribute(false);
            a.setContributeBlockedReason("Дедлайн истёк, кампания завершена.");
            a.setCanPreparePayout(false);
            a.setPayoutBlockedReason("Цель не достигнута, выплата недоступна.");
            a.setCanFinalize(false);
        } else {
            a.setCanContribute(false);
            a.setCanPreparePayout(false);
            a.setCanFinalize(false);
        }
        return a;
    }
}
