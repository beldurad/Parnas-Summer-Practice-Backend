package org.example.parnasservice.dto.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvmTransactionRequest {

    private long chainId;
    private String from;
    private String to;
    private String data;
    private String valueRaw;
    private String gasLimit;
    private String maxFeePerGas;
    private String maxPriorityFeePerGas;
    private Instant expiresAt;
}
