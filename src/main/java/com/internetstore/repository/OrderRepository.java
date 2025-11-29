package com.internetstore.repository;

import com.internetstore.entity.Order;
import com.internetstore.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing Order entities in MongoDB.
 * Provides CRUD operations and custom queries for user-specific and status-specific orders.
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    /**
     * Finds paginated orders for a specific user.
     * Useful for displaying user's order history with pagination.
     *
     * @param userId   ID of the user
     * @param pageable Pagination information (page number, size, sort)
     * @return A page of orders for the given user
     */
    Page<Order> findByUserId(String userId, Pageable pageable);

    /**
     * Finds all orders with a specific status.
     * Useful for admin dashboards or batch processing of orders.
     *
     * @param status Status of the orders to retrieve
     * @return List of orders matching the status
     */
    List<Order> findByStatus(OrderStatus status);
}
