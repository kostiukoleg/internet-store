package com.internetstore.service;

import com.internetstore.dto.request.AddressRequest;
import com.internetstore.dto.response.AddressResponse;
import com.internetstore.entity.Address;
import com.internetstore.exception.ResourceNotFoundException;
import com.internetstore.mapper.AddressMapper;
import com.internetstore.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final MongoTemplate mongoTemplate;

    // ----------------------------
    // Public API
    // ----------------------------

    /**
     * Create a new address for a user.
     * Handles default address logic automatically.
     */
    @Transactional
    public AddressResponse createAddress(AddressRequest request, String userId) {
        boolean makeDefault = shouldBeDefault(request, userId);

        if (makeDefault) {
            resetUserDefaultAddress(userId);
        }

        Address address = addressMapper.fromRequest(request);
        address.setUserId(userId);
        address.setIsDefault(makeDefault);

        Address saved = addressRepository.save(address);
        return addressMapper.toResponse(saved);
    }

    /**
     * Return all addresses of a user.
     */
    public List<AddressResponse> getUserAddresses(String userId) {
        return addressMapper.toResponseList(
                addressRepository.findByUserId(userId)
        );
    }

    /**
     * Return the default address of a user.
     */
    public AddressResponse getDefaultAddress(String userId) {
        Address address = addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Default address not found"));
        return addressMapper.toResponse(address);
    }

    /**
     * Delete an address only if it belongs to the given user.
     */
    @Transactional
    public void deleteAddress(String addressId, String userId) {
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found or does not belong to user"));
        addressRepository.delete(address);
    }

    // ----------------------------
    // Private helpers
    // ----------------------------

    /**
     * Determine whether the new address should be default.
     * True if either:
     *   - request explicitly asks for default
     *   - user has no existing addresses
     */
    private boolean shouldBeDefault(AddressRequest request, String userId) {
        boolean requestedDefault = Boolean.TRUE.equals(request.getIsDefault());
        boolean noExistingAddresses = addressRepository.countByUserId(userId) == 0;
        return requestedDefault || noExistingAddresses;
    }

    /**
     * Reset all existing default addresses for the user.
     */
    @Transactional
    public void resetUserDefaultAddress(String userId) {
        Query query = Query.query(
                Criteria.where("userId").is(userId)
                        .and("isDefault").is(true)
        );
        Update update = new Update().set("isDefault", false);
        mongoTemplate.updateMulti(query, update, Address.class);
    }
}