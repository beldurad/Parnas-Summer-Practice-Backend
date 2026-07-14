package org.example.parnasservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayoutDistributionPreviewItem {

    private String walletAddress;
    private TokenAmount contributionAmount;
    private double sharePercent;
    private TokenAmount payoutAmount;
}
