package com.internetstore.service;

import com.internetstore.dto.request.CartItemRequest;
import com.internetstore.dto.response.CartResponse;
import com.internetstore.entity.Cart;
import com.internetstore.entity.CartItem;
import com.internetstore.entity.Product;
import com.internetstore.exception.BadRequestException;
import com.internetstore.exception.ResourceNotFoundException;
import com.internetstore.mapper.MapStructMapper;
import com.internetstore.repository.CartRepository;
import com.internetstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final MapStructMapper mapper;

    /**
     * Get current user's cart. If the cart does not exist, return an empty cart with zero total.
     */
    public CartResponse getCart() {
        String userId = getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(Cart.builder()
                        .userId(userId)
                        .totalPrice(BigDecimal.ZERO)
                        .updatedAt(LocalDateTime.now())
                        .build());

        return mapper.cartToCartResponse(cart);
    }

    /**
     * Add an item to the cart. If the item already exists, update the quantity.
     * @param request contains productId and quantity
     */
    @Transactional
    public CartResponse addItemToCart(CartItemRequest request) {
        String userId = getCurrentUserId();

        // Fetch product and check stock
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock");
        }

        // Fetch or create cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(Cart.builder()
                        .userId(userId)
                        .totalPrice(BigDecimal.ZERO)
                        .build());

        // Check if item already exists
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Increment quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            // Add new item
            CartItem newItem = CartItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(request.getQuantity())
                    .image(product.getImages() != null && !product.getImages().isEmpty() ?
                            product.getImages().get(0) : null)
                    .build();
            cart.getItems().add(newItem);
        }

        // Update total and timestamp
        recalculateCartTotal(cart);
        cart.setUpdatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);
        return mapper.cartToCartResponse(savedCart);
    }

    /**
     * Update the quantity of a cart item. Remove item if quantity <= 0
     */
    @Transactional
    public CartResponse updateCartItem(String productId, Integer quantity) {
        String userId = getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        recalculateCartTotal(cart);
        cart.setUpdatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);
        return mapper.cartToCartResponse(savedCart);
    }

    /**
     * Remove a specific item from the cart
     */
    @Transactional
    public void removeItemFromCart(String productId) {
        String userId = getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (removed) {
            recalculateCartTotal(cart);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        }
    }

    /**
     * Clear the entire cart for current user
     */
    @Transactional
    public void clearCart() {
        String userId = getCurrentUserId();
        cartRepository.deleteByUserId(userId);
    }

    /**
     * Recalculate the total price of the cart
     */
    private void recalculateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    /**
     * Get the current user's identifier.
     * Currently using email as userId; ideally, should fetch actual user ID from DB.
     */
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
