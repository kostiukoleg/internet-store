package com.internetstore.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void contextLoads() {
        assertNotNull(securityConfig);
    }

    @Test
    void passwordEncoderBeanExists() {
        assertNotNull(securityConfig.passwordEncoder());
    }
}