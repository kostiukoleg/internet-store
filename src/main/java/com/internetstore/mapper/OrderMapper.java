package com.internetstore.mapper;

import com.internetstore.dto.response.OrderItemResponse;
import com.internetstore.dto.response.OrderResponse;
import com.internetstore.entity.Order;
import com.internetstore.entity.OrderItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    OrderItemResponse toItemResponse(OrderItem item);

    List<OrderItemResponse> toItemResponseList(List<OrderItem> items);
}
