package com.internetstore.config;

import com.internetstore.security.AuthEntryPointJwt;
import com.internetstore.security.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class.
 * <p>
 * Configures password encoding, authentication manager, JWT filter, 
 * and HTTP security rules for the application.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final AuthEntryPointJwt unauthorizedHandler;

    /**
     * Bean for password encoding using BCrypt.
     * <p>
     * This bean is used by Spring Security to hash and verify passwords.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean for AuthenticationManager.
     * <p>
     * Uses the modern Spring Security 6 approach with AuthenticationConfiguration
     * instead of the deprecated WebSecurityConfigurerAdapter.
     *
     * @param authConfig the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs creating the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Defines the security filter chain for HTTP requests.
     * <p>
     * - Disables CSRF (since this is a REST API)
     * - Configures exception handling with a JWT unauthorized handler
     * - Sets session management to stateless (for JWT authentication)
     * - Configures public and protected endpoints
     * - Adds the JWT authentication filter before UsernamePasswordAuthenticationFilter
     *
     * @param http the HttpSecurity object to configure
     * @return the built SecurityFilterChain
     * @throws Exception if an error occurs while building the security chain
     */
    @Bean
    public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) 
            throws Exception {

        http
            // Disable CSRF for REST APIs
            .csrf(csrf -> csrf.disable())
            
            // Configure exception handling for unauthorized requests
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
            
            // Use stateless session management (no HTTP session)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Define public and protected endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Public authentication endpoints
                .anyRequest().authenticated() // All other endpoints require authentication
            );

        // Add JWT filter before the default UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
