package org.example.parnasservice.service;

import java.util.UUID;
import java.util.stream.Collectors;
import org.example.parnasservice.dto.response.ContributionListResponse;
import org.example.parnasservice.entity.Contribution;
import org.example.parnasservice.exception.ResourceNotFoundException;
import org.example.parnasservice.mapper.EntityMapper;
import org.example.parnasservice.repository.CampaignRepository;
import org.example.parnasservice.repository.ContributionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final CampaignRepository campaignRepository;
    private final ContributionRepository contributionRepository;
    private final EntityMapper entityMapper;

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
}
