package com.feyza.defect_tracking.dto.auth;

import com.feyza.defect_tracking.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 5, message = "Password must be at least 5 characters long")
    private String password;

    @NotNull(message = "Role is required")
    private Role role; // ADMIN, TESTER, DEVELOPER[cite: 1]
}
