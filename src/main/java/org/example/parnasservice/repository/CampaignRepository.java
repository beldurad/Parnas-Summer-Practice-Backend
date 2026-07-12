package org.example.parnasservice.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.example.parnasservice.entity.Campaign;
import org.example.parnasservice.entity.enums.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {

    Page<Campaign> findByCreatorId(UUID creatorId, Pageable pageable);

    List<Campaign> findByCreatorId(UUID creatorId);

    Page<Campaign> findByCreatorIdAndStatusIn(UUID creatorId, List<CampaignStatus> statuses, Pageable pageable);

    Page<Campaign> findByStatusIn(List<CampaignStatus> statuses, Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE " +
           "(:query IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:statuses IS NULL OR c.status IN :statuses) AND " +
           "(:creatorId IS NULL OR c.creator.id = :creatorId)")
    Page<Campaign> searchCampaigns(@Param("query") String query,
                                    @Param("statuses") List<CampaignStatus> statuses,
                                    @Param("creatorId") UUID creatorId,
                                    Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.status = 'OPEN' AND c.deadline < :now")
    List<Campaign> findExpiredOpenCampaigns(@Param("now") Instant now);

    List<Campaign> findTop6ByStatusOrderByCreatedAtDesc(CampaignStatus status);

    @Query("SELECT c FROM Campaign c WHERE c.status = 'OPEN' ORDER BY c.raisedAmount DESC")
    List<Campaign> findTop6ByStatusOrderByRaisedAmountDesc(CampaignStatus status);

}
