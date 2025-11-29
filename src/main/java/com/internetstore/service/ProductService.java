package com.internetstore.service;

import com.internetstore.dto.response.ProductFilterResponse;
import com.internetstore.dto.response.ProductResponse;
import com.internetstore.entity.Product;
import com.internetstore.exception.ResourceNotFoundException;
import com.internetstore.mapper.MapStructMapper;
import com.internetstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MapStructMapper mapper;

    /**
     * Fetch paginated products with optional filtering by search term, category, and price range.
     *
     * @param filter the filter object containing search, category, minPrice, maxPrice, page, size
     * @return a page of ProductResponse objects
     */
    public Page<ProductResponse> getProducts(ProductFilterResponse filter) {
        // Create pageable object sorted by creation date descending
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(),
                Sort.by("createdAt").descending());

        Page<Product> products;

        // Priority: search term > category + price range > category only > price range only > all active
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            // Full-text search on name/description
            products = productRepository.searchProducts(filter.getSearch(), pageable);
        } else if (filter.getCategory() != null && filter.getMinPrice() != null && filter.getMaxPrice() != null) {
            // Filter by category and price range
            products = productRepository.findByCategoryAndPriceBetweenAndActiveTrue(
                    filter.getCategory(), filter.getMinPrice(), filter.getMaxPrice(), pageable);
        } else if (filter.getCategory() != null) {
            // Filter by category only
            products = productRepository.findByCategoryAndActiveTrue(filter.getCategory(), pageable);
        } else if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
            // Filter by price range only
            products = productRepository.findByPriceBetweenAndActiveTrue(
                    filter.getMinPrice(), filter.getMaxPrice(), pageable);
        } else {
            // Default: all active products
            products = productRepository.findByActiveTrue(pageable);
        }

        // Map Product entities to DTO responses
        return products.map(mapper::productToProductResponse);
    }

    /**
     * Fetch a single product by ID.
     *
     * @param id the product ID
     * @return ProductResponse DTO
     * @throws ResourceNotFoundException if product is not found
     */
    public ProductResponse getProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapper.productToProductResponse(product);
    }

    /**
     * Create a new product.
     *
     * @param productResponse the product data from request
     * @return created ProductResponse
     */
    public ProductResponse createProduct(ProductResponse productResponse) {
        // Convert DTO to entity
        Product product = mapper.productResponseToProduct(productResponse);
        // Save entity to database
        Product savedProduct = productRepository.save(product);
        // Convert entity back to DTO for response
        return mapper.productToProductResponse(savedProduct);
    }

    /**
     * Update an existing product.
     *
     * @param id              the product ID
     * @param productResponse the updated product data
     * @return updated ProductResponse
     * @throws ResourceNotFoundException if product is not found
     */
    public ProductResponse updateProduct(String id, ProductResponse productResponse) {
        // Fetch existing product
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update fields
        existingProduct.setName(productResponse.getName());
        existingProduct.setDescription(productResponse.getDescription());
        existingProduct.setPrice(productResponse.getPrice());
        existingProduct.setStockQuantity(productResponse.getStockQuantity());
        existingProduct.setCategory(productResponse.getCategory());
        existingProduct.setImages(productResponse.getImages());
        existingProduct.setActive(productResponse.isActive());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        // Save updated entity
        Product updatedProduct = productRepository.save(existingProduct);
        return mapper.productToProductResponse(updatedProduct);
    }

    /**
     * Delete a product by ID.
     *
     * @param id the product ID
     * @throws ResourceNotFoundException if product is not found
     */
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }
}
