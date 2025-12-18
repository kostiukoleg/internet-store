package com.internetstore.service;

import com.internetstore.dto.request.UserUpdateRequest;
import com.internetstore.dto.response.AddressResponse;
import com.internetstore.dto.response.UserResponse;
import com.internetstore.entity.User;
import com.internetstore.exception.ResourceNotFoundException;
import com.internetstore.mapper.AddressMapper;
import com.internetstore.mapper.UserMapper;
import com.internetstore.repository.AddressRepository;
import com.internetstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserMapper userMapper;
    private final AddressMapper addressMapper;

    // -----------------------
    // Public API
    // -----------------------

    public UserResponse getCurrentUser() {
        return userMapper.toUserResponse(fetchCurrentUser());
    }

    public UserResponse updateUser(UserUpdateRequest updateRequest) {
        User user = fetchCurrentUser();
        applyUserUpdates(user, updateRequest);
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<AddressResponse> getUserAddresses() {
        String userId = fetchCurrentUser().getId();
        return addressRepository.findByUserId(userId).stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    // -----------------------
    // Private helpers
    // -----------------------

    private User fetchCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private void applyUserUpdates(User user, UserUpdateRequest update) {
        Optional.ofNullable(update.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(update.getLastName()).ifPresent(user::setLastName);
    }

    public String getCurrentUserId() {
        return fetchCurrentUser().getId();
    }
}