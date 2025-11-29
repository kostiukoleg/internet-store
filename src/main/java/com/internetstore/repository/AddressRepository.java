package com.internetstore.repository;

import com.internetstore.entity.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Address entities in MongoDB.
 * Provides basic CRUD operations plus some custom queries.
 */
@Repository
public interface AddressRepository extends MongoRepository<Address, String> {

    /**
     * Finds all addresses belonging to a specific user.
     *
     * @param userId ID of the user
     * @return List of addresses
     */
    List<Address> findByUserId(String userId);

    /**
     * Finds the default address for a specific user.
     *
     * @param userId ID of the user
     * @return Optional containing the default address if present
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(String userId);

    /**
     * Deletes an address by user ID and address ID.
     * Ensures that only addresses belonging to the user can be deleted.
     *
     * @param userId ID of the user
     * @param id ID of the address
     */
    void deleteByUserIdAndId(String userId, String id);
}
