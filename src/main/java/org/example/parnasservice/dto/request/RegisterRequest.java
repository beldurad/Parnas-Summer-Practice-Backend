package org.example.parnasservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$")
    private String walletAddress;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,32}$")
    private String username;

    @NotBlank
    @Size(min = 1, max = 100)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    private String lastName;

}
