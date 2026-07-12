package org.example.parnasservice.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinalizeExpiredCampaignsResponse {

    private UUID jobId;
    private String status;
    private int candidatesCount;

}
