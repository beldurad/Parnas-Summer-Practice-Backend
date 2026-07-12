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
import org.example.parnasservice.entity.enums.CampaignStatus;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
public class Campaign {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(name = "short_description", length = 300)
    private String shortDescription;

    @Column(length = 5000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "target_amount", nullable = false, length = 78)
    private String targetAmount;

    @Column(name = "raised_amount", nullable = false, length = 78)
    private String raisedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CampaignStatus status;

    @Column(nullable = false)
    private Instant deadline;

    @Column(name = "chain_id", nullable = false)
    private int chainId;

    @Column(name = "contract_address", length = 42)
    private String contractAddress;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
