package com.internetstore.repository;

import com.internetstore.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing User entities in MongoDB.
 * Supports authentication and user management operations.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find a user by their email address.
     * Useful for authentication (login) and user profile retrieval.
     *
     * @param email the user's email
     * @return optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email already exists.
     * Useful for registration to prevent duplicate accounts.
     *
     * @param email the user's email
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);
}
