package com.internetstore.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Configuration properties for JWT (JSON Web Token) settings.
 * <p>
 * This class binds properties from the application's configuration
 * (e.g., application.yml or application.properties) with prefix `app.jwt`.
 * <p>
 * Example in application.properties:
 * <pre>
 * app.jwt.secret=your-secret-key
 * app.jwt.expiration=3600000
 * app.jwt.refresh-expiration=86400000
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtProperties {

    /**
     * Secret key used to sign and validate JWT tokens.
     */
    private String secret;

    /**
     * Expiration time in milliseconds for access tokens.
     */
    private long expiration;

    /**
     * Expiration time in milliseconds for refresh tokens.
     */
    private long refreshExpiration;
}
