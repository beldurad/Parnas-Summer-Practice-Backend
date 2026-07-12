package org.example.parnasservice.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.parnasservice.dto.UserSummary;

@Getter
@Setter
@NoArgsConstructor
public class CampaignDetail {

    private UUID id;
    private String contractAddress;
    private String title;
    private String shortDescription;
    private String description;
    private String status;
    private UserSummary creator;
    private CampaignFinancials financials;
    private Instant createdAt;
    private Instant deadline;
    private boolean blockchainDataFresh;
    private Long blockchainDataAsOfBlock;
    private List<PayoutSummary> recentPayouts;
    private List<BlockchainTransactionResponse> recentTransactions;

}
