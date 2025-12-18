package com.internetstore.mapper;

import com.internetstore.dto.response.CartResponse;
import com.internetstore.entity.Cart;
import com.internetstore.entity.CartItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CartMapper {

    CartResponse toResponse(Cart cart);

    CartResponse.CartItemResponse toItemResponse(CartItem item);

    List<CartResponse.CartItemResponse> toItemResponseList(List<CartItem> items);
}
