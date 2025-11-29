package com.internetstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for health check endpoints.
 * Provides public and optionally secure endpoints to monitor the service status.
 */
@RestController
@RequestMapping("/health") // Base path for health check endpoints
@Tag(name = "Health", description = "Health check endpoints") // OpenAPI/Swagger documentation
public class HealthController {

    /**
     * GET /health
     * Public health check endpoint.
     * Can be used by monitoring tools to verify the service is running.
     *
     * @return HTTP 200 OK with a status map
     */
    @GetMapping
    @Operation(summary = "Public health check")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "internet-store"
        ));
    }

    /**
     * GET /health/secure
     * Secure health check endpoint.
     * Requires authentication and is useful for verifying service availability for authorized users.
     *
     * @return HTTP 200 OK with a status map
     */
    @GetMapping("/secure")
    @Operation(summary = "Secure health check (requires authentication)")
    public ResponseEntity<Map<String, String>> secureHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "This is a secure endpoint"
        ));
    }
}
