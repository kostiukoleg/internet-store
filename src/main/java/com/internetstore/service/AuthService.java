package com.internetstore.service;

import com.internetstore.dto.request.LoginRequest;
import com.internetstore.dto.request.RegisterRequest;
import com.internetstore.dto.response.AuthResponse;
import com.internetstore.entity.User;
import com.internetstore.exception.BadRequestException;
import com.internetstore.mapper.MapStructMapper;
import com.internetstore.repository.UserRepository;
import com.internetstore.security.JwtUtils;
import com.internetstore.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

/**
 * Service for handling authentication and registration.
 * Encapsulates Spring Security authentication and JWT token generation.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager; // Handles authentication
    private final UserRepository userRepository; // User data access
    private final PasswordEncoder passwordEncoder; // Password hashing
    private final JwtUtils jwtUtils; // JWT creation and validation
    private final MapStructMapper mapper; // DTO <-> Entity mapping

    /**
     * Authenticate user and generate JWT token.
     *
     * @param loginRequest contains email and password
     * @return AuthResponse with JWT and user info
     */
    public AuthResponse login(LoginRequest loginRequest) {
        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Set authentication context for current session
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Get user details
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.user();

        // Build response DTO
        return AuthResponse.builder()
                .accessToken(jwt)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Register a new user and automatically authenticate.
     *
     * @param registerRequest contains user registration info
     * @return AuthResponse with JWT and user info
     * @throws BadRequestException if email is already taken
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check for existing email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already taken!");
        }

        // Map registration request to user entity
        User user = mapper.registerRequestToUser(registerRequest);

        // Encode password
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Save user in database
        User savedUser = userRepository.save(user);

        // Automatically authenticate new user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Build response DTO
        return AuthResponse.builder()
                .accessToken(jwt)
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .roles(savedUser.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .build();
    }
}
