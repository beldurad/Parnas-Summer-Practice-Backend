package org.example.parnasservice.repository;

import java.util.List;
import java.util.UUID;
import org.example.parnasservice.entity.Payout;
import org.example.parnasservice.entity.enums.PayoutStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutRepository extends JpaRepository<Payout, UUID> {

    Page<Payout> findByCampaignId(UUID campaignId, Pageable pageable);

    List<Payout> findByCampaignId(UUID campaignId);

    boolean existsByCampaignIdAndStatus(UUID campaignId, PayoutStatus status);
}
