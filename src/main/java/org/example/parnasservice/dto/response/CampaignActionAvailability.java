package org.example.parnasservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CampaignActionAvailability {

    private boolean canContribute;
    private String contributeBlockedReason;
    private boolean canPreparePayout;
    private String payoutBlockedReason;
    private boolean canViewManagement;
    private boolean canFinalize;

}
