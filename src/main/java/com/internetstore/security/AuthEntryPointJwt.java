package com.internetstore.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Entry point for handling unauthorized access attempts.
 *
 * <p>This class is invoked by Spring Security when:
 * <ul>
 *   <li>A protected endpoint is accessed</li>
 *   <li>No valid authentication is present</li>
 * </ul>
 *
 * <p>It sends a 401 Unauthorized response instead of redirecting
 * to a login page (important for REST APIs).
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * Called whenever an unauthenticated user tries to access
     * a protected resource.
     *
     * @param request       incoming HTTP request
     * @param response      HTTP response to be sent
     * @param authException authentication error
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        logger.error("Unauthorized access: {}", authException.getMessage());

        response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Error: Unauthorized"
        );
    }
}