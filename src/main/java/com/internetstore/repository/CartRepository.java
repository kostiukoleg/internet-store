package com.internetstore.repository;

import com.internetstore.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing Cart entities in MongoDB.
 * Provides CRUD operations and custom queries for user-specific carts.
 */
@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    /**
     * Finds a cart belonging to a specific user.
     *
     * @param userId ID of the user
     * @return Optional containing the cart if it exists
     */
    Optional<Cart> findByUserId(String userId);

    /**
     * Deletes a cart belonging to a specific user.
     * Ensures that only the user's cart is deleted.
     *
     * @param userId ID of the user
     */
    void deleteByUserId(String userId);
}
