package org.example.parnasservice.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.parnasservice.dto.response.PayoutDetail;
import org.example.parnasservice.dto.response.PayoutDistributionListResponse;
import org.example.parnasservice.dto.response.PayoutDistributionResponse;
import org.example.parnasservice.dto.response.PayoutListResponse;
import org.example.parnasservice.dto.response.PayoutSummary;
import org.example.parnasservice.entity.Payout;
import org.example.parnasservice.entity.PayoutDistribution;
import org.example.parnasservice.exception.ResourceNotFoundException;
import org.example.parnasservice.mapper.EntityMapper;
import org.example.parnasservice.repository.ContributionRepository;
import org.example.parnasservice.repository.PayoutDistributionRepository;
import org.example.parnasservice.repository.PayoutRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayoutService {

    private final PayoutRepository payoutRepository;
    private final PayoutDistributionRepository distributionRepository;
    private final ContributionRepository contributionRepository;
    private final EntityMapper entityMapper;

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
        return detail;
    }

    @Transactional(readOnly = true)
    public PayoutDistributionListResponse getPayoutDistributions(UUID campaignId, UUID payoutId,
                                                                  int page, int pageSize) {
        payoutRepository.findById(payoutId)
            .orElseThrow(() -> new ResourceNotFoundException("Выплата не найдена"));

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<PayoutDistribution> distributions = distributionRepository.findByPayoutId(payoutId, pageable);

        List<PayoutDistributionResponse> items = distributions.getContent().stream()
            .map(entityMapper::toPayoutDistributionResponse)
            .collect(Collectors.toList());

        return new PayoutDistributionListResponse(items, entityMapper.toPaginationMeta(distributions));
    }
}
