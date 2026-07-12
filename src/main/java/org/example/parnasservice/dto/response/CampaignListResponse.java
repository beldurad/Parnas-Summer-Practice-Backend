package org.example.parnasservice.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignListResponse {

    private List<CampaignListItem> items;
    private PaginationMeta pagination;

}
