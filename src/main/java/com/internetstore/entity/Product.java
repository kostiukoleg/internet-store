package com.internetstore.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a product in the store.
 * Contains all necessary product information such as pricing, stock, and metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {

    /** Unique identifier for the product */
    @Id
    private String id;

    /** Name of the product; included in text search */
    @TextIndexed
    private String name;

    /** Description of the product; included in text search */
    @TextIndexed
    private String description;

    /** Price of the product */
    private BigDecimal price;

    /** Available quantity in stock */
    private Integer stockQuantity;

    /** Category name for filtering / classification */
    private String category;

    /** List of image URLs for the product */
    private List<String> images;

    /** Average rating from reviews */
    private BigDecimal rating;

    /** Number of reviews submitted for this product */
    private Integer reviewCount;

    /** Whether the product is active and visible in the store */
    private boolean active;

    /** Timestamp when the product was created */
    private LocalDateTime createdAt;

    /** Timestamp when the product was last updated */
    private LocalDateTime updatedAt;
}
