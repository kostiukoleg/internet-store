package com.internetstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that checks for JWT tokens in incoming HTTP requests.
 * <p>
 * It validates the JWT, extracts the user email, loads user details,
 * and sets the Spring Security context so that authenticated users can access secured endpoints.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils; // JWT utility for parsing and validation
    private final UserDetailsServiceImpl userDetailsService; // Custom service to load user info

    /**
     * Main filter method that executes once per request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract JWT token from the request header
            String jwt = parseJwt(request);

            // Validate the token
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Extract the email from JWT claims
                String email = jwtUtils.getEmailFromJwtToken(jwt);

                // Load user details from DB or cache
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Create authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Attach request info to authentication
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Log detailed errors without exposing sensitive info
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header (Bearer scheme).
     *
     * @param request HTTP request
     * @return JWT token or null if not present
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Remove "Bearer " prefix
            return headerAuth.substring(7);
        }

        return null;
    }
}
