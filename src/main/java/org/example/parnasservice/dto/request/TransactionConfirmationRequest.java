package org.example.parnasservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionConfirmationRequest {

    @NotBlank
    @Pattern(regexp = "^0x[a-fA-F0-9]{64}$")
    private String transactionHash;
}
