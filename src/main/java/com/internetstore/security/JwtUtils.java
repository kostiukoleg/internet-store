package com.internetstore.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for generating and validating JWT tokens.
 * <p>
 * This class is responsible for creating JWT tokens using HS512 signature,
 * extracting the email (subject) from tokens, and validating token integrity and expiration.
 */
@Slf4j
@Component
public class JwtUtils {

    // Secret key for signing JWTs; must be sufficiently long for HS512 (at least 64 bytes)
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Expiration in milliseconds
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Returns a Key object from the secret for signing/verifying JWTs.
     * Uses HMAC-SHA512.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token for authenticated user.
     *
     * @param authentication the authentication object containing principal
     * @return JWT token as String
     */
    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail()) // store email as subject
                .setIssuedAt(now) // issued time
                .setExpiration(expiryDate) // expiration time
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // HS512 signing
                .compact();
    }

    /**
     * Extract the email (subject) from the JWT token.
     *
     * @param token JWT token
     * @return email stored in token
     */
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate the JWT token for integrity, expiration, and correct signature.
     *
     * @param authToken JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true; // token is valid
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
