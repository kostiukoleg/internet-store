package com.internetstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for user login request.
 * Contains credentials used for authentication.
 */
@Data
public class LoginRequest {

    /**
     * User email.
     * - Must not be blank
     * - Must be a valid email format
     */
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * User password (raw).
     * Will be validated and matched against encoded password.
     */
    @NotBlank(message = "Password must not be empty")
    private String password;
}
