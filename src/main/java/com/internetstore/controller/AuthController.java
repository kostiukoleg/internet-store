package com.internetstore.controller;

import com.internetstore.dto.request.LoginRequest;
import com.internetstore.dto.request.RegisterRequest;
import com.internetstore.dto.response.AuthResponse;
import com.internetstore.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST controller.
 * Handles login and registration endpoints for the application.
 */
@RestController
@RequestMapping("/api/auth") // Base path for authentication endpoints
@RequiredArgsConstructor // Lombok generates constructor for final fields
@Tag(name = "Authentication", description = "Authentication endpoints") // Swagger/OpenAPI documentation
public class AuthController {

    // Service handling authentication logic (login, register)
    private final AuthService authService;

    /**
     * POST /auth/login
     * Handles user login.
     *
     * @param loginRequest DTO containing username/email and password
     * @return AuthResponse containing JWT token and user info
     */
    @PostMapping("/login")
    @Operation(summary = "User login") // OpenAPI documentation
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Delegate login logic to AuthService
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /auth/register
     * Handles new user registration.
     *
     * @param registerRequest DTO containing user registration info (username, email, password, etc.)
     * @return AuthResponse containing JWT token and user info
     */
    @PostMapping("/register")
    @Operation(summary = "User registration") // OpenAPI documentation
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Delegate registration logic to AuthService
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
}