package org.example.parnasservice.repository;

import java.util.UUID;
import org.example.parnasservice.entity.PayoutDistribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutDistributionRepository extends JpaRepository<PayoutDistribution, UUID> {

    Page<PayoutDistribution> findByPayoutId(UUID payoutId, Pageable pageable);
}
