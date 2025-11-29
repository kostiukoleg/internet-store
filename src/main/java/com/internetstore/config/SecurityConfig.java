package com.internetstore.config;

import com.internetstore.security.AuthTokenFilter;
import com.internetstore.security.UserDetailsServiceImpl;
import com.internetstore.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the Internet Store backend.
 * Configures authentication, authorization, JWT filter, CORS, and password encoding.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    // Custom user details service for loading user-specific data
    private final UserDetailsServiceImpl userDetailsService;

    // Utility class for JWT token operations (generate, validate, parse)
    private final JwtUtils jwtUtils;

    // Allowed CORS origins (configurable via application.properties)
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String[] allowedOrigins;

    /**
     * JWT authentication filter bean.
     * Intercepts requests and validates JWT token in the Authorization header.
     */
    @Bean
    @Order(1)
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    /**
     * DAO authentication provider for Spring Security.
     * Uses UserDetailsService and PasswordEncoder to authenticate users from the database.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expose AuthenticationManager as a Spring Bean.
     * Needed for manual authentication in services (e.g., login endpoint).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Password encoder bean using BCrypt hashing.
     * Strong hashing algorithm for storing passwords securely.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures Spring Security filter chain.
     * - Enables CORS
     * - Disables CSRF (safe for stateless JWT-based auth)
     * - Stateless session management
     * - Configures endpoint access rules
     * - Adds JWT authentication filter before UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        try {
            http
                    // Enable CORS with custom configuration
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    // Disable CSRF because we are using JWTs
                    .csrf(AbstractHttpConfigurer::disable)
                    // Stateless session: do not store session on server
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    // Authorization rules
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers(
                                    "/auth/**",                // Public authentication endpoints
                                    "/swagger-ui/**",          // Swagger UI
                                    "/v3/api-docs/**",         // OpenAPI docs
                                    "/api-docs/**",
                                    "/swagger-ui.html",
                                    "/webjars/**",
                                    "/swagger-resources/**",
                                    "/products/**",            // Public products listing
                                    "/actuator/health",        // Actuator endpoints for monitoring
                                    "/actuator/info"
                            ).permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN") // Admin-only endpoints
                            .anyRequest().authenticated()                 // All other requests require authentication
                    );

            // Register custom authentication provider
            http.authenticationProvider(authenticationProvider());

            // Add JWT token filter before UsernamePasswordAuthenticationFilter
            http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

            return http.build();
        } catch (Exception e) {
            // Wrap checked exceptions into runtime for simplicity
            throw new RuntimeException("Failed to configure security", e);
        }
    }

    /**
     * CORS configuration bean.
     * Defines allowed origins, methods, headers, and credentials.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Set allowed origins from configuration or allow all
        if (allowedOrigins != null && allowedOrigins.length > 0) {
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        } else {
            // Use wildcard pattern if no origins specified
            configuration.setAllowedOriginPatterns(List.of("*"));
        }

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allowed HTTP headers from client
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Headers exposed to clients
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));

        // Allow cookies / credentials
        configuration.setAllowCredentials(true);

        // Preflight request cache duration (in seconds)
        configuration.setMaxAge(3600L);

        // Register the configuration for all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
