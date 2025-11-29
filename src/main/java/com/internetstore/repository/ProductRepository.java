package com.internetstore.repository;

import com.internetstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for managing Product entities in MongoDB.
 * Supports filtering, searching, and pagination.
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    /**
     * Find all active products in a specific category with pagination.
     *
     * @param category the category to filter
     * @param pageable pagination information
     * @return paged list of active products in the category
     */
    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);

    /**
     * Find all active products with pagination.
     *
     * @param pageable pagination information
     * @return paged list of active products
     */
    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * Find all active products within a price range with pagination.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination information
     * @return paged list of products
     */
    @Query("{'active': true, 'price': {$gte: ?0, $lte: ?1}}")
    Page<Product> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Find active products within a category and price range with pagination.
     *
     * @param category the category to filter
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination information
     * @return paged list of products
     */
    @Query("{'active': true, 'category': ?0, 'price': {$gte: ?1, $lte: ?2}}")
    Page<Product> findByCategoryAndPriceBetweenAndActiveTrue(String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Search active products by name or description (case-insensitive) with pagination.
     * Uses regex search on MongoDB; consider indexing 'name' and 'description' fields for performance.
     *
     * @param searchTerm the search string
     * @param pageable   pagination information
     * @return paged list of matching products
     */
    @Query("{$and: [{'active': true}, {$or: [{'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}]}]}")
    Page<Product> searchProducts(String searchTerm, Pageable pageable);

    /**
     * Find products by a list of IDs.
     * Useful for cart and order services to fetch product details.
     *
     * @param ids list of product IDs
     * @return list of matching products
     */
    List<Product> findByIdIn(List<String> ids);
}
