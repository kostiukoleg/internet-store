package com.internetstore.service;

import com.internetstore.dto.request.LoginRequest;
import com.internetstore.dto.request.RegisterRequest;
import com.internetstore.dto.response.AuthResponse;
import com.internetstore.entity.User;
import com.internetstore.exception.BadRequestException;
import com.internetstore.mapper.UserMapper;
import com.internetstore.repository.UserRepository;
import com.internetstore.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Service responsible for authentication logic:
 * - user login
 * - user registration
 * - JWT generation
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * Spring Security authentication manager.
     * Delegates authentication to AuthenticationProvider (DAO provider).
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Repository for accessing users in MongoDB.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder (BCrypt).
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Utility class for JWT generation and validation.
     */
    private final JwtUtils jwtUtils;

    /**
     * MapStruct mapper for converting DTOs to entities.
     */
    private final UserMapper userMapper;

    /**
     * Authenticate user credentials and return JWT.
     *
     * @param loginRequest email + password
     * @return AuthResponse with JWT and user info
     */
    public AuthResponse login(LoginRequest loginRequest) {

        // Authenticate user using Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Store authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        /*
         * IMPORTANT:
         * This cast is VALID ONLY if:
         * - User implements UserDetails
         * - UserDetailsService returns User
         */
        User user = (User) authentication.getPrincipal();

        // Generate JWT token using user email as subject
        String jwt = jwtUtils.generateJwtToken(user.getEmail());

        return buildAuthResponse(user, jwt);
    }

    /**
     * Register a new user and immediately issue JWT.
     *
     * @param registerRequest registration data
     * @return AuthResponse with JWT
     */
    public AuthResponse register(RegisterRequest registerRequest) {

        // Prevent duplicate email registration
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }

        // Map request DTO to User entity
        User user = userMapper.fromRegisterRequest(registerRequest);

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Enable user account
        user.setEnabled(true);

        // Persist user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(savedUser.getEmail());

        return buildAuthResponse(savedUser, jwt);
    }

    /**
     * Build authentication response DTO.
     *
     * @param user authenticated user
     * @param jwt generated JWT token
     * @return AuthResponse
     */
    private AuthResponse buildAuthResponse(User user, String jwt) {
        return AuthResponse.builder()
                .accessToken(jwt)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(
                        user.getRoles().stream()
                                .map(Enum::name)
                                .collect(Collectors.toList())
                )
                .build();
    }
}