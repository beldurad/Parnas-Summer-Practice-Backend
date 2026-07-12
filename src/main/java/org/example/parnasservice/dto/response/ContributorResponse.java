package org.example.parnasservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContributorResponse {

    private String walletAddress;
    private TokenAmount totalContribution;
    private double sharePercent;
    private int contributionsCount;

}
