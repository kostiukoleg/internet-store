package com.internetstore.controller;

import com.internetstore.dto.request.CartItemRequest;
import com.internetstore.dto.response.CartResponse;
import com.internetstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the shopping cart.
 * Handles operations such as getting the cart, adding items, updating quantities,
 * removing items, and clearing the cart.
 */
@RestController
@RequestMapping("/cart") // Base URL for cart-related endpoints
@RequiredArgsConstructor // Generates constructor for final fields
@SecurityRequirement(name = "bearerAuth") // Swagger/OpenAPI JWT security scheme
@Tag(name = "Cart", description = "Shopping cart management endpoints") // API documentation
public class CartController {

    private final CartService cartService;

    /**
     * GET /cart
     * Retrieves the current authenticated user's shopping cart.
     *
     * @return ResponseEntity with CartResponse containing all cart items
     */
    @GetMapping
    @Operation(summary = "Get user's cart")
    public ResponseEntity<CartResponse> getCart() {
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    /**
     * POST /cart/items
     * Adds an item to the authenticated user's cart.
     *
     * @param request DTO containing productId and quantity
     * @return ResponseEntity with updated CartResponse
     */
    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartResponse> addItemToCart(@Valid @RequestBody CartItemRequest request) {
        CartResponse cart = cartService.addItemToCart(request);
        return ResponseEntity.ok(cart);
    }

    /**
     * PUT /cart/items/{productId}
     * Updates the quantity of a specific cart item.
     *
     * @param productId ID of the product in the cart
     * @param quantity  New quantity to set
     * @return ResponseEntity with updated CartResponse
     */
    @PutMapping("/items/{productId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable String productId,
                                                       @RequestParam Integer quantity) {
        CartResponse cart = cartService.updateCartItem(productId, quantity);
        return ResponseEntity.ok(cart);
    }

    /**
     * DELETE /cart/items/{productId}
     * Removes a specific item from the authenticated user's cart.
     *
     * @param productId ID of the product to remove
     * @return ResponseEntity with HTTP 204 No Content on success
     */
    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable String productId) {
        cartService.removeItemFromCart(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /cart
     * Clears all items from the authenticated user's cart.
     *
     * @return ResponseEntity with HTTP 204 No Content on success
     */
    @DeleteMapping
    @Operation(summary = "Clear cart")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}