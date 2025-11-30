package com.internetstore.repository;

import com.internetstore.entity.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Address documents in MongoDB.
 * Provides standard CRUD operations and user-scoped lookup queries.
 */
@Repository
public interface AddressRepository extends MongoRepository<Address, String> {

    /**
     * Returns all addresses owned by the given user.
     *
     * @param userId user identifier
     * @return list of addresses
     */
    List<Address> findByUserId(String userId);

    /**
     * Returns the user's default address if it exists.
     *
     * @param userId user identifier
     * @return optional containing the default address
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(String userId);

    /**
     * Finds a specific address belonging to the given user.
     *
     * @param userId user identifier
     * @param id     address identifier
     * @return optional containing the address if found
     */
    Optional<Address> findByUserIdAndId(String userId, String id);

    /**
     * Deletes a specific address belonging to the given user.
     *
     * @param userId user identifier
     * @param id     address identifier
     */
    void deleteByUserIdAndId(String userId, String id);

    /**
     * Counts how many addresses the user has.
     *
     * @param userId user identifier
     * @return number of address documents
     */
    long countByUserId(String userId);
}