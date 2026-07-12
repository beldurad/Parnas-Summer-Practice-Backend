package org.example.parnasservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCampaignRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 10, max = 5000)
    private String description;

    @NotBlank
    @Pattern(regexp = "^[1-9][0-9]*$")
    private String targetAmountRaw;

    @NotNull
    @Future
    private Instant deadline;

    @NotNull
    @Min(1)
    private Integer chainId;

}
