package org.example.parnasservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CampaignFinancials {

    private TokenAmount targetAmount;
    private TokenAmount raisedAmount;
    private TokenAmount remainingAmount;
    private double progressPercent;
    private int contributorsCount;
    private int payoutsCount;
    private TokenAmount totalProfitDistributed;

}
