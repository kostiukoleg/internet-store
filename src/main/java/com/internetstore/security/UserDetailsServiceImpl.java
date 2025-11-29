package com.internetstore.security;

import com.internetstore.entity.User;
import com.internetstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of Spring Security's UserDetailsService.
 * <p>
 * Responsible for loading UserDetails (UserPrincipal) from the database
 * using the email as the username.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by email (used as username for authentication).
     *
     * @param email the email of the user
     * @return UserPrincipal implementing UserDetails
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the user from MongoDB using email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Wrap the User entity in UserPrincipal which implements UserDetails
        return new UserPrincipal(user);
    }
}
