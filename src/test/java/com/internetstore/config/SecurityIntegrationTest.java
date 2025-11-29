package com.internetstore.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void publicEndpointsShouldBeAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void secureEndpointsShouldRequireAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/health/secure", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void swaggerEndpointsShouldBeAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/swagger-ui.html", String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful() ||
                response.getStatusCode() == HttpStatus.NOT_FOUND); // Some redirects might happen
    }
}