package com.internetstore.mapper;

import com.internetstore.dto.request.RegisterRequest;
import com.internetstore.dto.response.*;
import com.internetstore.entity.*;
import com.internetstore.enums.UserRole;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for converting between entity objects and DTOs.
 * Uses Spring component model and ignores unmapped targets by default.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructMapper {

    // -------------------------------
    // USER MAPPINGS
    // -------------------------------

    /**
     * Maps User entity to UserResponse DTO.
     * Only maps necessary fields and converts roles to string list.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    @Mapping(target = "createdAt", source = "createdAt")
    UserResponse userToUserResponse(User user);

    /**
     * Maps RegisterRequest DTO to User entity.
     * Sets defaults for roles, enabled, and timestamps.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true) // default ROLE_USER will be used
    @Mapping(target = "addressIds", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User registerRequestToUser(RegisterRequest request);

    // -------------------------------
    // ADDRESS MAPPINGS
    // -------------------------------

    AddressResponse addressToAddressResponse(Address address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Address addressResponseToAddress(AddressResponse addressResponse);

    // -------------------------------
    // PRODUCT MAPPINGS
    // -------------------------------

    ProductResponse productToProductResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Product productResponseToProduct(ProductResponse productResponse);

    // -------------------------------
    // CART MAPPINGS
    // -------------------------------

    CartResponse cartToCartResponse(Cart cart);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "image", source = "image")
    CartResponse.CartItemResponse cartItemToCartItemResponse(CartItem cartItem);

    // -------------------------------
    // ORDER MAPPINGS
    // -------------------------------

    OrderResponse orderToOrderResponse(Order order);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "image", source = "image")
    OrderItemResponse orderItemToOrderItemResponse(OrderItem orderItem);

    // -------------------------------
    // HELPER METHODS
    // -------------------------------

    /**
     * Converts user roles enum to a list of role names.
     */
    default List<String> mapRoles(List<UserRole> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Enum::name)
                .toList();
    }

    // -------------------------------
    // LIST MAPPINGS
    // -------------------------------
    // NOTE: MapStruct cannot use "@Mapping(target=".", ignore=true)" for lists.
    // Instead, define list mapping methods that automatically call individual mapping methods.

    List<AddressResponse> addressListToAddressResponseList(List<Address> addresses);

    List<CartResponse.CartItemResponse> cartItemListToCartItemResponseList(List<CartItem> cartItems);

    List<OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> orderItems);
}
