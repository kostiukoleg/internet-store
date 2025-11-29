package com.internetstore.service;

import com.internetstore.dto.request.AddressRequest;
import com.internetstore.dto.response.AddressResponse;
import com.internetstore.entity.Address;
import com.internetstore.mapper.MapStructMapper;
import com.internetstore.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Address operations.
 * Uses MapStructMapper for DTO ↔ Entity conversions.
 */
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final MapStructMapper mapper;

    /**
     * Create a new address for a user.
     */
    public AddressResponse createAddress(AddressRequest request, String userId) {
        // MapStruct doesn’t have AddressRequest → Address yet, so we build manually
        Address address = Address.builder()
                .userId(userId)
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .isDefault(request.isDefault())
                .build();

        return mapper.addressToAddressResponse(addressRepository.save(address));
    }

    /**
     * Get all addresses for a user.
     */
    public List<AddressResponse> getUserAddresses(String userId) {
        return mapper.addressListToAddressResponseList(addressRepository.findByUserId(userId));
    }

    /**
     * Get default address for a user.
     */
    public AddressResponse getDefaultAddress(String userId) {
        Optional<Address> addressOpt = addressRepository.findById(userId);
        Address address = addressOpt.orElseThrow(() ->
                new RuntimeException("Address not found with id: " + userId));
        return mapper.addressToAddressResponse(address);
    }

    /**
     * Delete an address by ID.
     */
    public void deleteAddress(String id) {
        addressRepository.deleteById(id);
    }
}