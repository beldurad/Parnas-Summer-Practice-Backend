package org.example.parnasservice.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HomeCampaignCard {

    private UUID id;
    private String title;
    private String shortDescription;
    private String status;
    private TokenAmount targetAmount;
    private TokenAmount raisedAmount;
    private double progressPercent;
    private Instant deadline;

}
