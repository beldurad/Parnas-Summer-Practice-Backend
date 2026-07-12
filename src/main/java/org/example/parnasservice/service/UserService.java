package org.example.parnasservice.service;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.parnasservice.dto.response.CampaignSummary;
import org.example.parnasservice.dto.response.DashboardResponse;
import org.example.parnasservice.dto.response.MyContributionItem;
import org.example.parnasservice.dto.response.MyContributionListResponse;
import org.example.parnasservice.dto.response.PublicUserProfile;
import org.example.parnasservice.dto.response.TokenAmount;
import org.example.parnasservice.dto.response.UserProfileResponse;
import org.example.parnasservice.dto.response.CampaignListResponse;
import org.example.parnasservice.dto.response.CampaignListItem;
import org.example.parnasservice.entity.Campaign;
import org.example.parnasservice.entity.Contribution;
import org.example.parnasservice.entity.Payout;
import org.example.parnasservice.entity.User;
import org.example.parnasservice.entity.enums.CampaignStatus;
import org.example.parnasservice.entity.enums.ContributionStatus;
import org.example.parnasservice.entity.enums.PayoutStatus;
import org.example.parnasservice.exception.ResourceNotFoundException;
import org.example.parnasservice.mapper.EntityMapper;
import org.example.parnasservice.repository.CampaignRepository;
import org.example.parnasservice.repository.ContributionRepository;
import org.example.parnasservice.repository.PayoutRepository;
import org.example.parnasservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final ContributionRepository contributionRepository;
    private final PayoutRepository payoutRepository;
    private final EntityMapper entityMapper;

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        return entityMapper.toUserProfile(user);
    }

    @Transactional(readOnly = true)
    public PublicUserProfile getPublicUserProfile(UUID userId, int page, int pageSize) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Campaign> campaignsPage = campaignRepository.findByCreatorId(userId, pageable);

        List<CampaignListItem> items = campaignsPage.getContent().stream()
            .map(entityMapper::toCampaignListItem)
            .collect(Collectors.toList());

        PublicUserProfile profile = new PublicUserProfile();
        profile.setUser(entityMapper.toUserSummary(user));
        profile.setCreatedCampaigns(items);
        profile.setPagination(entityMapper.toPaginationMeta(campaignsPage));
        return profile;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UUID userId, int itemsLimit) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        List<CampaignSummary> createdCampaigns = campaignRepository.findByCreatorId(userId).stream()
            .map(c -> {
                int cc = contributionRepository.countByCampaignIdAndStatus(c.getId(), ContributionStatus.CONFIRMED);
                List<Payout> ps = payoutRepository.findByCampaignId(c.getId());
                String profit = ps.stream()
                    .filter(p -> p.getStatus() == PayoutStatus.CONFIRMED)
                    .map(Payout::getProfitAmount)
                    .reduce((a, b) -> new BigInteger(a).add(new BigInteger(b)).toString())
                    .orElse("0");
                return entityMapper.toCampaignSummary(c, cc, ps.size(), profit);
            })
            .limit(itemsLimit)
            .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(0, itemsLimit);
        List<MyContributionItem> contributions = contributionRepository
            .findByContributorUserId(userId, pageable).getContent().stream()
            .map(c -> {
                int cc = contributionRepository.countByCampaignIdAndStatus(c.getCampaign().getId(), ContributionStatus.CONFIRMED);
                List<Payout> ps = payoutRepository.findByCampaignId(c.getCampaign().getId());
                String profit = ps.stream()
                    .filter(p -> p.getStatus() == PayoutStatus.CONFIRMED)
                    .map(Payout::getProfitAmount)
                    .reduce((a, b) -> new BigInteger(a).add(new BigInteger(b)).toString())
                    .orElse("0");
                return entityMapper.toMyContributionItem(c, cc, ps.size(), profit);
            })
            .collect(Collectors.toList());

        DashboardResponse response = new DashboardResponse();
        response.setUser(entityMapper.toUserProfile(user));
        response.setCreatedCampaigns(createdCampaigns);
        response.setContributions(contributions);

        DashboardResponse.DashboardAggregates aggr = new DashboardResponse.DashboardAggregates();
        aggr.setCreatedCampaignsCount(campaignRepository.findByCreatorId(userId).size());
        aggr.setSupportedCampaignsCount(contributions.size());
        aggr.setTotalContributed(new TokenAmount("0", 18, "VFT", "0"));
        aggr.setTotalDividendsReceived(new TokenAmount("0", 18, "VFT", "0"));
        response.setAggregates(aggr);

        return response;
    }

    @Transactional(readOnly = true)
    public MyContributionListResponse getMyContributions(UUID userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Contribution> contributionsPage = contributionRepository.findByContributorUserId(userId, pageable);

        List<MyContributionItem> items = contributionsPage.getContent().stream()
            .map(c -> {
                int cc = contributionRepository.countByCampaignIdAndStatus(c.getCampaign().getId(), ContributionStatus.CONFIRMED);
                List<Payout> ps = payoutRepository.findByCampaignId(c.getCampaign().getId());
                String profit = ps.stream()
                    .filter(p -> p.getStatus() == PayoutStatus.CONFIRMED)
                    .map(Payout::getProfitAmount)
                    .reduce((a, b) -> new BigInteger(a).add(new BigInteger(b)).toString())
                    .orElse("0");
                return entityMapper.toMyContributionItem(c, cc, ps.size(), profit);
            })
            .collect(Collectors.toList());

        return new MyContributionListResponse(items, entityMapper.toPaginationMeta(contributionsPage));
    }

    @Transactional(readOnly = true)
    public CampaignListResponse getMyCampaigns(UUID userId, List<CampaignStatus> statuses,
                                                String sort, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Campaign> campaignPage;

        if (statuses != null && !statuses.isEmpty()) {
            campaignPage = campaignRepository.findByCreatorIdAndStatusIn(userId, statuses, pageable);
        } else {
            campaignPage = campaignRepository.findByCreatorId(userId, pageable);
        }

        List<CampaignListItem> items = campaignPage.getContent().stream()
            .map(entityMapper::toCampaignListItem)
            .collect(Collectors.toList());

        return new CampaignListResponse(items, entityMapper.toPaginationMeta(campaignPage));
    }
}
