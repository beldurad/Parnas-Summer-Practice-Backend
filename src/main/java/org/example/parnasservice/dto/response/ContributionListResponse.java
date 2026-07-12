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
public class ContributionListResponse {

    private List<ContributionResponse> items;
    private PaginationMeta pagination;

}
