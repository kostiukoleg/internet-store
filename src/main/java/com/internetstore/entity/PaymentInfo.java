package com.internetstore.entity;

import com.internetstore.enums.PaymentMethod;
import com.internetstore.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents payment information for an order.
 * Stores the method, transaction ID, and payment timestamp.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {

    /**
     * Payment method used (e.g., CREDIT_CARD, PAYPAL, STRIPE).
     */
    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    /**
     * Transaction ID returned by the payment gateway.
     * Useful for reconciliation and refunds.
     */
    private String transactionId;

    /**
     * Date and time when payment was completed.
     */
    private LocalDateTime paidAt;
}
