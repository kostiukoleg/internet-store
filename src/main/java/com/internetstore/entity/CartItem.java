package com.internetstore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a single item in a shopping cart.
 * Stores product reference, quantity, and price information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    /**
     * Unique identifier of the product.
     * Must correspond to a product in the catalog.
     */
    private String productId;

    /**
     * Name of the product at the time it was added to the cart.
     * Helps preserve history if product name changes later.
     */
    private String productName;

    /**
     * Price of a single unit of the product.
     * Should be set when the item is added to the cart.
     */
    private BigDecimal price;

    /**
     * Quantity of the product in the cart.
     * Should never be negative. Use validation in service layer.
     */
    private Integer quantity;

    /**
     * Optional image URL for the product.
     * Helps display items in the cart without extra queries.
     */
    private String image;

    // Optional improvements:

    // 1. Null-safety for quantity and price:
    // You can add @Builder.Default for quantity = 1 and price = BigDecimal.ZERO
    // to avoid NullPointerExceptions in calculations.

    // 2. Helper method for total price of this item:
    public BigDecimal getTotalPrice() {
        if (price == null || quantity == null) return BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
