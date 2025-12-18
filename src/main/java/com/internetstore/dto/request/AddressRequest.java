package com.internetstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO used for creating or updating a user address.
 *
 * <p>
 * This DTO is validated at the controller layer before being
 * passed to the service layer.
 * </p>
 */
@Data
public class AddressRequest {

    /**
     * Street address (e.g. "123 Main St, Apt 4B").
     */
    @NotBlank(message = "Street must not be blank")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    private String street;

    /**
     * City name.
     */
    @NotBlank(message = "City must not be blank")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    /**
     * State or province.
     * Optional depending on country.
     */
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    /**
     * ZIP or postal code.
     */
    @NotBlank(message = "ZIP code must not be blank")
    @Pattern(
        regexp = "^[A-Za-z0-9\\- ]{3,12}$",
        message = "Invalid ZIP/postal code format"
    )
    private String zipCode;

    /**
     * Country code in ISO-2 format (e.g. "UA", "US").
     */
    @NotBlank(message = "Country must not be blank")
    @Pattern(
        regexp = "^[A-Z]{2}$",
        message = "Country must be a valid ISO-2 code (e.g. UA, US)"
    )
    private String country;

    /**
     * Indicates whether this address should be set as default.
     * Defaults to {@code false} if not provided.
     */
    private Boolean isDefault;
}
