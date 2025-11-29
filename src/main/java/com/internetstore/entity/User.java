package com.internetstore.entity;

import com.internetstore.enums.UserRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User entity representing application users.
 * Implements Spring Security's UserDetails for authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    /** Unique user ID */
    @Id
    private String id;

    /** Unique email, used as username for authentication */
    @Indexed(unique = true)
    private String email;

    /** Encrypted password */
    private String password;

    /** First name of the user */
    private String firstName;

    /** Last name of the user */
    private String lastName;

    /**
     * User roles. Default is ROLE_USER.
     * Used for authorization and granted authorities.
     */
    @Builder.Default
    private List<UserRole> roles = List.of(UserRole.ROLE_USER);

    /**
     * Addresses linked to the user.
     * Using @DBRef for MongoDB references (optional: could embed addresses instead).
     */
    @DBRef
    @Builder.Default
    private List<Address> addressIds = List.of();

    /** Whether the user account is enabled */
    private boolean enabled;

    /** User creation timestamp */
    private LocalDateTime createdAt;

    /** Last update timestamp */
    private LocalDateTime updatedAt;

    /**
     * Convert roles to Spring Security authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    /** Email is used as username for authentication */
    @Override
    public String getUsername() {
        return email;
    }

    /** Always non-expired for now */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** Always non-locked for now */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** Credentials never expire for now */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
