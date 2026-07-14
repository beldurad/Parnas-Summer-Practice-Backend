package org.example.parnasservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContributionAcceptedResponse {

    private ContributionResponse contribution;
    private String statusUrl;
}
