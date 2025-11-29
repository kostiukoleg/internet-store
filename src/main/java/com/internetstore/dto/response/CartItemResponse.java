package com.internetstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private String productId;
    private String productName;
    private BigDecimal price;
    @Builder.Default
    private Integer quantity = 1;
    private String image;
}
