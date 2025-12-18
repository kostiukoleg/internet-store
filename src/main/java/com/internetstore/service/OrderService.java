package com.internetstore.service;

import com.internetstore.dto.request.CreateOrderRequest;
import com.internetstore.dto.response.OrderResponse;
import com.internetstore.entity.*;
import com.internetstore.enums.OrderStatus;
import com.internetstore.exception.BadRequestException;
import com.internetstore.exception.ResourceNotFoundException;
import com.internetstore.mapper.OrderMapper;
import com.internetstore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for handling Orders.
 * Handles order creation, retrieval, status updates, and order queries by status.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository; // Needed to fetch current user by email
    private final OrderMapper orderMapper;

    /**
     * Create a new order from the user's cart.
     * @param request Order creation request containing shipping info.
     * @return OrderResponse DTO with order details.
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = getCurrentUser(); // Fetch full user entity

        // Fetch user's cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // Fetch and validate shipping address
        Address shippingAddress = addressRepository.findById(request.getShippingAddressId())
                .filter(addr -> addr.getUserId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));

        // Calculate subtotal and verify stock
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: " + cartItem.getProductId()));

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for product: " + product.getName());
            }

            // Deduct stock and save product
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            subtotal = subtotal.add(cartItem.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        // Calculate tax (10%) and total
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal shipping = request.getShipping() != null ? request.getShipping() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).add(shipping);

        // Build order entity
        Order order = Order.builder()
                .userId(user.getId())
                .status(OrderStatus.PENDING)
                .items(cart.getItems().stream()
                        .map(this::convertToOrderItem)
                        .collect(Collectors.toList()))
                .subtotal(subtotal)
                .tax(tax)
                .shipping(shipping)
                .total(total)
                .shippingAddress(shippingAddress)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Persist order
        Order savedOrder = orderRepository.save(order);

        // Clear user's cart
        cartRepository.deleteById(cart.getId());

        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Get paginated orders for the current user.
     */
    public Page<OrderResponse> getUserOrders(int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserId(user.getId(), pageable)
                .map(orderMapper::toResponse);
    }

    /**
     * Get a single order by ID.
     * Validates ownership or admin access.
     */
    public OrderResponse getOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User user = getCurrentUser();

        // Only owner or admin can access
        if (!order.getUserId().equals(user.getId()) && !isAdmin()) {
            throw new AccessDeniedException("Access denied");
        }

        return orderMapper.toResponse(order);
    }

    /**
     * Update the status of an order. Admin only.
     */
    @Transactional
    public OrderResponse updateOrderStatus(String orderId, OrderStatus status) {
        if (!isAdmin()) {
            throw new AccessDeniedException("Only admins can update order status");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    /**
     * Retrieve all orders with a specific status. Admin only.
     */
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        if (!isAdmin()) {
            throw new AccessDeniedException("Only admins can view all orders by status");
        }

        return orderRepository.findByStatus(status)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert a CartItem entity to an OrderItem entity.
     */
    private OrderItem convertToOrderItem(CartItem cartItem) {
        return OrderItem.builder()
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .image(cartItem.getImage())
                .build();
    }

    /**
     * Get the currently authenticated user entity from the database.
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    /**
     * Check if the currently authenticated user has admin role.
     */
    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
