package com.internetstore.controller;

import com.internetstore.dto.response.ProductFilterResponse;
import com.internetstore.dto.response.ProductResponse;
import com.internetstore.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for product management.
 * Provides endpoints for retrieving, creating, updating, and deleting products.
 * Admin endpoints are protected with role-based access control.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    /**
     * GET /products
     * Retrieve paginated products with optional filtering.
     *
     * @param filter filter object containing criteria (category, price range, etc.)
     * @return HTTP 200 OK with paginated list of products
     */
    @GetMapping
    @Operation(summary = "Get products with filtering")
    public ResponseEntity<Page<ProductResponse>> getProducts(ProductFilterResponse filter) {
        Page<ProductResponse> products = productService.getProducts(filter);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /products/{id}
     * Retrieve a single product by ID.
     *
     * @param id product ID
     * @return HTTP 200 OK with product details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        ProductResponse product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    /**
     * POST /products
     * Create a new product (Admin only).
     *
     * @param productRequest product request payload
     * @return HTTP 200 OK with created product
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Admin-only access
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create new product (Admin only)")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductResponse productRequest) {
        ProductResponse createdProduct = productService.createProduct(productRequest);
        return ResponseEntity.ok(createdProduct);
    }

    /**
     * PUT /products/{id}
     * Update an existing product (Admin only).
     *
     * @param id product ID
     * @param productRequest product request payload
     * @return HTTP 200 OK with updated product
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update product (Admin only)")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductResponse productRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * DELETE /products/{id}
     * Delete a product by ID (Admin only).
     *
     * @param id product ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete product (Admin only)")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
