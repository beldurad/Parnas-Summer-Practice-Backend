package org.example.parnasservice.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileResponse {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String walletAddress;
    private Instant createdAt;
    private Instant lastLoginAt;

}
