package org.example.parnasservice.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.parnasservice.dto.UserSummary;

@Getter
@Setter
@NoArgsConstructor
public class PublicUserProfile {

    private UserSummary user;
    private List<CampaignListItem> createdCampaigns;
    private PaginationMeta pagination;

}
