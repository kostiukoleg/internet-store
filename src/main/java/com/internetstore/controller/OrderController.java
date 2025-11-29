package com.internetstore.controller;

import com.internetstore.dto.request.CreateOrderRequest;
import com.internetstore.dto.response.OrderResponse;
import com.internetstore.enums.OrderStatus;
import com.internetstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for order management.
 * Handles order creation, retrieval, and status updates.
 * Admin endpoints are protected with role-based access control.
 */
@RestController
@RequestMapping("/orders") // Base path for all order-related endpoints
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Swagger/OpenAPI annotation for JWT security
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /orders
     * Create a new order from the current user's cart.
     *
     * @param request order creation request
     * @return HTTP 200 OK with created order
     */
    @PostMapping
    @Operation(summary = "Create new order from cart")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }

    /**
     * GET /orders
     * Retrieve paginated list of the current user's orders.
     *
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @return HTTP 200 OK with paginated order responses
     */
    @GetMapping
    @Operation(summary = "Get user's orders")
    public ResponseEntity<Page<OrderResponse>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderResponse> orders = orderService.getUserOrders(page, size);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /orders/{orderId}
     * Retrieve a single order by its ID.
     *
     * @param orderId ID of the order
     * @return HTTP 200 OK with order details
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        OrderResponse order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * PUT /orders/{orderId}/status
     * Update the status of an order (ADMIN only).
     *
     * @param orderId ID of the order
     * @param status new status to set
     * @return HTTP 200 OK with updated order
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')") // Only accessible to admin users
    @Operation(summary = "Update order status (Admin only)")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable String orderId,
                                                           @RequestParam OrderStatus status) {
        OrderResponse order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    /**
     * GET /orders/admin/status/{status}
     * Retrieve all orders filtered by status (ADMIN only).
     *
     * @param status order status to filter
     * @return HTTP 200 OK with a list of orders
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get orders by status (Admin only)")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
}
