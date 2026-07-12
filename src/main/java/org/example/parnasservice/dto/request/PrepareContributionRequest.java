package org.example.parnasservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrepareContributionRequest {

    @NotBlank
    @Pattern(regexp = "^[1-9][0-9]*$")
    private String amountRaw;

    @NotNull
    @Min(1)
    private Integer chainId;
}
