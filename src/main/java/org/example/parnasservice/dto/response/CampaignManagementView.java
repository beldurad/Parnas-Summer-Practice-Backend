package org.example.parnasservice.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CampaignManagementView {

    private CampaignDetail campaign;
    private CampaignActionAvailability actions;
    private List<ContributionResponse> recentContributions;
    private List<PayoutSummary> payouts;

}
