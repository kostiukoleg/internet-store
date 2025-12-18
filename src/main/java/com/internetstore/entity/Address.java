package com.internetstore.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Address entity representing a user's delivery or billing address.
 *
 * <p>
 * Each address belongs to exactly one user.
 * A user may have multiple addresses, but only one can be marked as default.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "addresses")
@CompoundIndex(
    name = "user_default_idx",
    def = "{ 'userId': 1, 'isDefault': 1 }"
)
public class Address {

    /**
     * Unique identifier of the address document.
     */
    @Id
    private String id;

    /**
     * ID of the user who owns this address.
     */
    @Indexed
    private String userId;

    /**
     * Street address (e.g. "123 Main St, Apt 4B").
     */
    private String street;

    /**
     * City name.
     */
    private String city;

    /**
     * State or province (optional, country-dependent).
     */
    private String state;

    /**
     * ZIP or postal code.
     */
    private String zipCode;

    /**
     * Country name (ISO recommended, e.g. "UA", "US").
     */
    private String country;

    /**
     * Indicates whether this address is the default for the user.
     *
     * <p>Each user should have at most one default address.</p>
     */
    private Boolean isDefault;

    /**
     * Timestamp when the address was created.
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Timestamp when the address was last updated.
     */
    @LastModifiedDate
    private Instant updatedAt;
}
