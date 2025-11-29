package com.internetstore.service;

import com.internetstore.dto.response.AddressResponse;
import com.internetstore.dto.response.UserResponse;
import com.internetstore.dto.request.UserUpdateRequest;
import com.internetstore.entity.Address;
import com.internetstore.entity.User;
import com.internetstore.exception.ResourceNotFoundException;
import com.internetstore.mapper.MapStructMapper;
import com.internetstore.repository.AddressRepository;
import com.internetstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final MapStructMapper mapper;

    /**
     * Get the current authenticated user's details.
     *
     * @return UserResponse DTO
     */
    public UserResponse getCurrentUser() {
        User user = getCurrentUserEntity();
        return mapper.userToUserResponse(user);
    }

    /**
     * Update the current user's first and/or last name.
     *
     * @param updateRequest DTO containing fields to update
     * @return updated UserResponse
     */
    public UserResponse updateUser(UserUpdateRequest updateRequest) {
        User user = getCurrentUserEntity();

        // Update only non-null fields
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        return mapper.userToUserResponse(updatedUser);
    }

    /**
     * Retrieve all addresses of the current user.
     *
     * @return list of AddressResponse DTOs
     */
    public List<AddressResponse> getUserAddresses() {
        User user = getCurrentUserEntity();
        return addressRepository.findByUserId(user.getId()).stream()
                .map(mapper::addressToAddressResponse)
                .toList();
    }

    /**
     * Add a new address for the current user.
     * Handles default address logic: first address or explicitly set default.
     *
     * @param addressResponse DTO containing address details
     * @return saved AddressResponse DTO
     */
    public AddressResponse addAddress(AddressResponse addressResponse) {
        User user = getCurrentUserEntity();

        Address address = mapper.addressResponseToAddress(addressResponse);
        address.setUserId(user.getId());

        // Fetch existing addresses to handle default logic
        List<Address> existingAddresses = addressRepository.findByUserId(user.getId());

        if (existingAddresses.isEmpty() || addressResponse.isDefault()) {
            address.setDefault(true);

            // If marked as default, unset previous default
            if (addressResponse.isDefault()) {
                existingAddresses.stream()
                        .filter(Address::isDefault)
                        .findFirst()
                        .ifPresent(addr -> {
                            addr.setDefault(false);
                            addressRepository.save(addr);
                        });
            }
        }

        Address savedAddress = addressRepository.save(address);
        return mapper.addressToAddressResponse(savedAddress);
    }

    /**
     * Delete a user's address by its ID.
     * Only addresses belonging to the current user can be deleted.
     *
     * @param addressId the ID of the address to delete
     */
    public void deleteAddress(String addressId) {
        User user = getCurrentUserEntity();
        addressRepository.deleteByUserIdAndId(user.getId(), addressId);
    }

    /**
     * Helper method to fetch the currently authenticated User entity.
     *
     * @return User entity
     * @throws ResourceNotFoundException if user not found in the database
     */
    private User getCurrentUserEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
