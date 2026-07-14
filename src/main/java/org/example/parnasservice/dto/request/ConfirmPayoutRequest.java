package org.example.parnasservice.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfirmPayoutRequest extends TransactionConfirmationRequest {

    @NotNull
    private UUID payoutId;
}
