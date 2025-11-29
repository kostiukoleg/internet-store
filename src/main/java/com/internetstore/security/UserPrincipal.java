package com.internetstore.security;

import com.internetstore.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom UserDetails implementation using Java Record.
 * Immutable and concise.
 */
public record UserPrincipal(User user) implements UserDetails {

    /**
     * Returns authorities granted to the user (roles).
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    /**
     * Returns user's password.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns username for authentication (email in this case).
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Returns user's email (useful for JWT generation).
     */
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    /**
     * Convenience method to get the user's ID.
     */
    public String getId() {
        return user.getId();
    }
}
