package org.example.parnasservice.dto.response;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HomePageResponse {

    private String platformName;
    private List<HomeCampaignCard> popularCampaigns;
    private List<HomeCampaignCard> activeCampaigns;
    private Instant generatedAt;

}
