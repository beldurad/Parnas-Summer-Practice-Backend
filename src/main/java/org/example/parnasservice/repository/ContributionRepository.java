package org.example.parnasservice.repository;

import java.util.List;
import java.util.UUID;
import org.example.parnasservice.entity.Contribution;
import org.example.parnasservice.entity.enums.ContributionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository extends JpaRepository<Contribution, UUID> {

    Page<Contribution> findByCampaignId(UUID campaignId, Pageable pageable);

    List<Contribution> findByCampaignId(UUID campaignId);

    List<Contribution> findByCampaignIdAndStatus(UUID campaignId, ContributionStatus status);

    Page<Contribution> findByContributorUserId(UUID userId, Pageable pageable);

    int countByCampaignId(UUID campaignId);

    int countByCampaignIdAndStatus(UUID campaignId, ContributionStatus status);
}
