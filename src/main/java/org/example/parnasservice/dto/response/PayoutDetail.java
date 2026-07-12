package org.example.parnasservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayoutDetail {

    private PayoutSummary payout;
    private CampaignSummary campaign;
    private TokenAmount distributedAmount;
    private TokenAmount roundingRemainder;
    private BlockchainTransactionResponse transaction;

}
