package org.example.parnasservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String walletAddress;
    private Instant createdAt;
    private Instant lastLoginAt;

}
