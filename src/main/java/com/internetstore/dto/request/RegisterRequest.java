package com.internetstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for user registration request.
 * Used to create a new user account.
 */
@Data
public class RegisterRequest {

    /**
     * User email (unique).
     */
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Raw password.
     * Should be encoded before persisting.
     */
    @NotBlank(message = "Password must not be empty")
    private String password;

    /**
     * User first name.
     */
    @NotBlank(message = "First name must not be empty")
    private String firstName;

    /**
     * User last name.
     */
    @NotBlank(message = "Last name must not be empty")
    private String lastName;
}
