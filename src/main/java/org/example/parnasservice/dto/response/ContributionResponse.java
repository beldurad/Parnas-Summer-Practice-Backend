package org.example.parnasservice.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContributionResponse {

    private UUID id;
    private UUID campaignId;
    private UUID contributorUserId;
    private String contributorWallet;
    private TokenAmount amount;
    private String status;
    private String transactionHash;
    private Instant createdAt;
    private Instant confirmedAt;

}
