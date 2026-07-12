package org.example.parnasservice.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String walletAddress;

}
