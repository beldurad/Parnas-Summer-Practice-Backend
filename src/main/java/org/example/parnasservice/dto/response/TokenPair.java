package org.example.parnasservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private int expiresIn;

}
