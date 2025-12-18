package com.internetstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that validates JWT tokens in incoming HTTP requests.
 * <p>
 * This filter is executed once per request (extends OncePerRequestFilter).
 * It extracts the JWT from the Authorization header, validates it, and sets the
 * authentication in the SecurityContext if the token is valid.
 */
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Filter logic executed for each HTTP request.
     *
     * @param request     incoming HTTP request
     * @param response    HTTP response
     * @param filterChain filter chain to pass control to the next filter
     * @throws ServletException in case of servlet errors
     * @throws IOException      in case of I/O errors
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip JWT parsing for public endpoints (e.g., authentication endpoints)
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Get the Authorization header
            String header = request.getHeader("Authorization");

            // Check if the header is not null and starts with "Bearer "
            if (header != null && header.startsWith("Bearer ")) {
                // Extract token by removing "Bearer " prefix
                String token = header.substring(7);

                // Validate JWT token
                if (jwtUtils.validateJwtToken(token)) {
                    // Extract the email (username) from the token
                    String email = jwtUtils.getEmailFromJwtToken(token);

                    // Load user details from UserDetailsService
                    var userDetails = userDetailsService.loadUserByUsername(email);

                    // Create an Authentication object using the user details and authorities
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // No credentials are needed here
                            userDetails.getAuthorities()
                    );

                    // Set additional details (IP address, session ID, etc.)
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // Log any exceptions during JWT processing
            logger.error("AuthTokenFilter error: " + e.getMessage(), e);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
