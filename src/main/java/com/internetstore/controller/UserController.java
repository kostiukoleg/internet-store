package com.internetstore.controller;

import com.internetstore.dto.request.AddressRequest;
import com.internetstore.dto.response.AddressResponse;
import com.internetstore.dto.request.UserUpdateRequest;
import com.internetstore.dto.response.UserResponse;
import com.internetstore.service.AddressService;
import com.internetstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    /**
     * GET /users/me
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * PUT /users/me
     */
    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserUpdateRequest updateRequest) {
        UserResponse updatedUser = userService.updateUser(updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * GET /users/me/addresses
     */
    @GetMapping("/me/addresses")
    @Operation(summary = "Get user addresses")
    public ResponseEntity<List<AddressResponse>> getUserAddresses() {
        String userId = userService.getCurrentUserId(); // helper in UserService
        List<AddressResponse> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * POST /users/me/addresses
     */
    @PostMapping("/me/addresses")
    @Operation(summary = "Add new address")
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        String userId = userService.getCurrentUserId(); // get current user's ID
        AddressResponse savedAddress = addressService.createAddress(request, userId);
        return ResponseEntity.ok(savedAddress);
    }

    /**
     * DELETE /users/me/addresses/{addressId}
     */
    @DeleteMapping("/me/addresses/{addressId}")
    @Operation(summary = "Delete address")
    public ResponseEntity<Void> deleteAddress(@PathVariable String addressId) {
        String userId = userService.getCurrentUserId(); // get current user's ID
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }
}
