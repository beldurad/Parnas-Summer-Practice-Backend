package org.example.parnasservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.parnasservice.entity.enums.TransactionStatus;
import org.example.parnasservice.entity.enums.TransactionType;

@Entity
@Table(name = "blockchain_transactions")
@Getter
@Setter
@NoArgsConstructor
public class BlockchainTransaction {

    @Id
    @Column(length = 66)
    private String hash;

    @Column(nullable = false, length = 20)
    private String network;

    @Column(name = "chain_id", nullable = false)
    private int chainId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "from_address", nullable = false, length = 42)
    private String from;

    @Column(name = "to_address", length = 42)
    private String to;

    @Column(name = "value_raw", length = 78)
    private String value;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(name = "block_hash", length = 66)
    private String blockHash;

    @Column(nullable = false)
    private int confirmations;

    @Column(name = "required_confirmations", nullable = false)
    private int requiredConfirmations;

    @Column(name = "gas_used", length = 78)
    private String gasUsed;

    @Column(name = "revert_reason", length = 500)
    private String revertReason;

    @Column(name = "explorer_url", length = 500)
    private String explorerUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;
}
