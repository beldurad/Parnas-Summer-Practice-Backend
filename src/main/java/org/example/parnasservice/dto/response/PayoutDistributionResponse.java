package org.example.parnasservice.dto.response;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayoutDistributionResponse {

    private UUID payoutId;
    private String walletAddress;
    private TokenAmount contributionAmount;
    private double sharePercent;
    private TokenAmount payoutAmount;
    private String transferStatus;
    private String transactionHash;

}
