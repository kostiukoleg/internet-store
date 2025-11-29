package com.internetstore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for the Internet Store Spring Boot application.
 *
 * Configurations:
 * 1. @SpringBootApplication - enables auto-configuration, component scanning, and configuration properties.
 * 2. @OpenAPIDefinition - defines API metadata for Swagger/OpenAPI documentation.
 * 3. @SecurityScheme - sets up JWT Bearer authentication for Swagger UI.
 * 4. @ComponentScan - ensures Spring scans all components, services, repositories under "com.internetstore".
 */
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Internet Store API",
                version = "1.0",
                description = "Internet Store Backend API"
        )
)
@SecurityScheme(
        name = "bearerAuth", // Name referenced in @SecurityRequirement in controllers
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT" // JWT token format
)
@ComponentScan(basePackages = "com.internetstore") // Optional: only needed if your package structure differs from default
public class InternetStoreApplication {

    /**
     * Spring Boot main method.
     * Starts the embedded server (Tomcat by default) and bootstraps the application context.
     */
    public static void main(String[] args) {
        SpringApplication.run(InternetStoreApplication.class, args);
    }
}
