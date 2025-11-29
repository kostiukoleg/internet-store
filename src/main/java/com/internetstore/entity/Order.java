package com.internetstore.entity;

import com.internetstore.enums.OrderStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a customer order.
 * Contains order items, pricing details, shipping info, and payment info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    /**
     * Unique identifier for the order.
     */
    @Id
    private String id;

    /**
     * ID of the user who placed the order.
     */
    private String userId;

    /**
     * Current status of the order.
     * Enum: PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
     */
    private OrderStatus status;

    /**
     * List of items in this order.
     * Each OrderItem represents a product and quantity at the time of purchase.
     */
    private List<OrderItem> items;

    /**
     * Sum of all item prices before tax and shipping.
     */
    private BigDecimal subtotal;

    /**
     * Tax applied to the order.
     */
    private BigDecimal tax;

    /**
     * Shipping cost for the order.
     */
    private BigDecimal shipping;

    /**
     * Total cost = subtotal + tax + shipping.
     * Should be calculated in service layer before saving.
     */
    private BigDecimal total;

    /**
     * Shipping address associated with this order.
     * Could be copied from the user's saved addresses at checkout time.
     */
    private Address shippingAddress;

    /**
     * Payment information (card, transaction ID, etc.)
     * Should be secure and possibly encrypted.
     */
    private PaymentInfo paymentInfo;

    /**
     * Order creation timestamp.
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp for the order.
     */
    private LocalDateTime updatedAt;

    // Optional improvements:

    // 1. Ensure subtotal, tax, shipping, and total are non-null (use BigDecimal.ZERO as default).
    // 2. Consider adding a helper method to recalculate total:
    //    public void calculateTotal() {
    //        this.total = (subtotal != null ? subtotal : BigDecimal.ZERO)
    //                   .add(tax != null ? tax : BigDecimal.ZERO)
    //                   .add(shipping != null ? shipping : BigDecimal.ZERO);
    //    }
    // 3. You may want to annotate createdAt with @Builder.Default:
    //    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
}
