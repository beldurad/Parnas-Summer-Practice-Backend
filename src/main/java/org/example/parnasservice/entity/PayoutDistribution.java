package org.example.parnasservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.parnasservice.entity.enums.TransferStatus;

@Entity
@Table(name = "payout_distributions")
@Getter
@Setter
@NoArgsConstructor
public class PayoutDistribution {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "payout_id", nullable = false)
    private Payout payout;

    @Column(name = "wallet_address", nullable = false, length = 42)
    private String walletAddress;

    @Column(name = "contribution_amount", nullable = false, length = 78)
    private String contributionAmount;

    @Column(name = "share_percent", nullable = false)
    private double sharePercent;

    @Column(name = "payout_amount", nullable = false, length = 78)
    private String payoutAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status", nullable = false, length = 20)
    private TransferStatus transferStatus;

    @Column(name = "transaction_hash", length = 66)
    private String transactionHash;
}
