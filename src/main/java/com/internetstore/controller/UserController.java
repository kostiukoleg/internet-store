package com.internetstore.controller;

import com.internetstore.dto.response.AddressResponse;
import com.internetstore.dto.request.UserUpdateRequest;
import com.internetstore.dto.response.UserResponse;
import com.internetstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for user management.
 * Provides endpoints for retrieving and updating user profile and addresses.
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    /**
     * GET /users/me
     * Retrieve the currently authenticated user's profile.
     *
     * @return HTTP 200 OK with user profile
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * PUT /users/me
     * Update the currently authenticated user's profile.
     *
     * @param updateRequest request containing updated user data
     * @return HTTP 200 OK with updated user profile
     */
    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserUpdateRequest updateRequest) {
        UserResponse updatedUser = userService.updateUser(updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * GET /users/me/addresses
     * Retrieve all addresses associated with the currently authenticated user.
     *
     * @return HTTP 200 OK with a list of addresses
     */
    @GetMapping("/me/addresses")
    @Operation(summary = "Get user addresses")
    public ResponseEntity<List<AddressResponse>> getUserAddresses() {
        List<AddressResponse> addresses = userService.getUserAddresses();
        return ResponseEntity.ok(addresses);
    }

    /**
     * POST /users/me/addresses
     * Add a new address for the currently authenticated user.
     *
     * @param addressDto request body containing address data
     * @return HTTP 200 OK with the saved address
     */
    @PostMapping("/me/addresses")
    @Operation(summary = "Add new address")
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressResponse addressDto) {
        AddressResponse savedAddress = userService.addAddress(addressDto);
        return ResponseEntity.ok(savedAddress);
    }

    /**
     * DELETE /users/me/addresses/{addressId}
     * Delete a specific address by ID for the currently authenticated user.
     *
     * @param addressId ID of the address to delete
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/me/addresses/{addressId}")
    @Operation(summary = "Delete address")
    public ResponseEntity<Void> deleteAddress(@PathVariable String addressId) {
        userService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
