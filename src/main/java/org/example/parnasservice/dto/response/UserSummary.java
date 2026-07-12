package org.example.parnasservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummary {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String walletAddress;

    public UserSummary() {}

}
