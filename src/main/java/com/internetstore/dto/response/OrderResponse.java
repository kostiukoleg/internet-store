package com.internetstore.dto.response;

import com.internetstore.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String userId;
    private OrderStatus status;

    @Builder.Default
    private List<OrderItemResponse> items = List.of();

    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal shipping = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    private AddressResponse shippingAddress;
    private LocalDateTime createdAt;
}