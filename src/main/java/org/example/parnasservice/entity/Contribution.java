package org.example.parnasservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.parnasservice.entity.enums.ContributionStatus;

@Entity
@Table(name = "contributions")
@Getter
@Setter
@NoArgsConstructor
public class Contribution {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "contributor_user_id")
    private User contributorUser;

    @Column(name = "contributor_wallet", nullable = false, length = 42)
    private String contributorWallet;

    @Column(nullable = false, length = 78)
    private String amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContributionStatus status;

    @Column(name = "transaction_hash", length = 66)
    private String transactionHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;
}
