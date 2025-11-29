package com.internetstore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a single item in an order.
 * Contains product info, price, quantity, and optional image.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * ID of the product.
     */
    private String productId;

    /**
     * Name of the product at the time of order.
     * Store the name to keep historical consistency in case the product name changes later.
     */
    private String productName;

    /**
     * Price of a single product unit at the time of order.
     */
    private BigDecimal price;

    /**
     * Quantity of the product in the order.
     * Should always be >= 1.
     */
    private Integer quantity;

    /**
     * Optional image URL of the product at the time of order.
     */
    private String image;

    /**
     * Helper method to calculate total price for this item.
     */
    public BigDecimal getTotalPrice() {
        if (price == null || quantity == null) return BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
