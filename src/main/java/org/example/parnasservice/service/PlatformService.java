package org.example.parnasservice.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.example.parnasservice.dto.response.HomePageResponse;
import org.example.parnasservice.entity.Campaign;
import org.example.parnasservice.entity.enums.CampaignStatus;
import org.example.parnasservice.mapper.EntityMapper;
import org.example.parnasservice.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformService {

    private final CampaignRepository campaignRepository;
    private final EntityMapper entityMapper;
    private final String platformName;

    public PlatformService(CampaignRepository campaignRepository,
                           EntityMapper entityMapper,
                           @Value("${parnas.platform.name}") String platformName) {
        this.campaignRepository = campaignRepository;
        this.entityMapper = entityMapper;
        this.platformName = platformName;
    }

    @Transactional(readOnly = true)
    public HomePageResponse getHomePage(int popularLimit, int activeLimit) {
        List<Campaign> popular = campaignRepository.findTop6ByStatusOrderByRaisedAmountDesc(CampaignStatus.OPEN);
        if (popularLimit > 0 && popular.size() > popularLimit) {
            popular = popular.subList(0, popularLimit);
        }

        List<Campaign> active = campaignRepository.findTop6ByStatusOrderByCreatedAtDesc(CampaignStatus.OPEN);
        if (activeLimit > 0 && active.size() > activeLimit) {
            active = active.subList(0, activeLimit);
        }

        HomePageResponse response = new HomePageResponse();
        response.setPlatformName(platformName);
        response.setPopularCampaigns(popular.stream()
            .map(entityMapper::toHomeCampaignCard)
            .collect(Collectors.toList()));
        response.setActiveCampaigns(active.stream()
            .map(entityMapper::toHomeCampaignCard)
            .collect(Collectors.toList()));
        response.setGeneratedAt(Instant.now());
        return response;
    }
}
