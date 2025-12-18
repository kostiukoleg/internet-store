package com.internetstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO representing a user address.
 *
 * <p>
 * Returned to clients when fetching or modifying addresses.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    /**
     * Unique identifier of the address.
     */
    private String id;

    /**
     * Street address (e.g. "123 Main St, Apt 4B").
     */
    private String street;

    /**
     * City name.
     */
    private String city;

    /**
     * State or province.
     * May be {@code null} depending on country.
     */
    private String state;

    /**
     * ZIP or postal code.
     */
    private String zipCode;

    /**
     * Country code in ISO-2 format (e.g. "UA", "US").
     */
    private String country;

    /**
     * Indicates whether this address is the default for the user.
     */
    private Boolean isDefault;
}
