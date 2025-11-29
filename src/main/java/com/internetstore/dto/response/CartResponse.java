package com.internetstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private String id;
    private String userId;

    @Builder.Default
    private List<CartItemResponse> items = List.of();

    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private String productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
        private String image;
    }
}