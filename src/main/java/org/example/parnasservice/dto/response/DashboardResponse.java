package org.example.parnasservice.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardResponse {

    private UserProfileResponse user;
    private List<CampaignSummary> createdCampaigns;
    private List<MyContributionItem> contributions;
    private DashboardAggregates aggregates;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DashboardAggregates {
        private int createdCampaignsCount;
        private int supportedCampaignsCount;
        private TokenAmount totalContributed;
        private TokenAmount totalDividendsReceived;
    }
}
