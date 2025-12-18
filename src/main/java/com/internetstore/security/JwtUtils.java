package com.internetstore.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utility class for generating and validating JSON Web Tokens (JWT).
 * <p>
 * Uses a secret key defined in JwtProperties for signing the tokens
 * and provides methods to generate tokens, extract email, and validate tokens.
 */
@Component
public class JwtUtils {

    private final JwtProperties properties;
    private Key key;

    /**
     * Constructor injection for JwtProperties.
     *
     * @param properties JWT configuration properties
     */
    public JwtUtils(JwtProperties properties) {
        this.properties = properties;
    }

    /**
     * Initializes the signing key from the secret in JwtProperties.
     * <p>
     * This method is called once after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for the given email (username).
     *
     * @param email the subject of the token (user identifier)
     * @return a signed JWT token string
     */
    public String generateJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date()) // Current time
                .setExpiration(new Date(System.currentTimeMillis() + properties.getExpiration())) // Expiration time
                .signWith(key, SignatureAlgorithm.HS256) // Sign with HMAC SHA-256
                .compact();
    }

    /**
     * Extracts the email (subject) from a JWT token.
     *
     * @param token the JWT token string
     * @return the email/username encoded in the token
     */
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Set signing key for validation
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validates a JWT token.
     *
     * @param token the JWT token string
     * @return true if the token is valid, false otherwise
     */
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // Throws exception if invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token is invalid, expired, or malformed
            // Optionally, you can log the error for debugging:
            // logger.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
