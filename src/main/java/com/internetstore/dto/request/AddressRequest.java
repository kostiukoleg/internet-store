package com.internetstore.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for creating/updating an Address.
 * Used to validate incoming JSON payloads.
 */
@Data
public class AddressRequest {
    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String country;

    private boolean isDefault;
}