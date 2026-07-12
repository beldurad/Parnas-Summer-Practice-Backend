package org.example.parnasservice.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CampaignListItem {

    private UUID id;
    private String title;
    private String shortDescription;
    private String status;
    private TokenAmount targetAmount;
    private TokenAmount raisedAmount;
    private double progressPercent;
    private Instant createdAt;
    private Instant deadline;

}
