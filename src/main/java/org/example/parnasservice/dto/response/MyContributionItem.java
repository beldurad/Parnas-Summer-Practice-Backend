package org.example.parnasservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyContributionItem {

    private ContributionResponse contribution;
    private CampaignSummary campaign;

}
