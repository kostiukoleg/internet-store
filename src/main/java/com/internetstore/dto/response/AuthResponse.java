package com.internetstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;

    @Builder.Default
    private String refreshToken = null;

    @Builder.Default
    private String type = "Bearer";

    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}