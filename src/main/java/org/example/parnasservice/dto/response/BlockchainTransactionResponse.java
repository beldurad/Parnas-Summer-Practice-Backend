package org.example.parnasservice.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BlockchainTransactionResponse {

    private String hash;
    private String network;
    private int chainId;
    private String type;
    private String status;
    private String from;
    private String to;
    private TokenAmount value;
    private Long blockNumber;
    private String blockHash;
    private int confirmations;
    private int requiredConfirmations;
    private String gasUsed;
    private String revertReason;
    private String explorerUrl;
    private Instant createdAt;
    private Instant confirmedAt;

}
