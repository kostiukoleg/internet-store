package com.internetstore.controller;

import com.internetstore.dto.request.AddressRequest;
import com.internetstore.dto.response.AddressResponse;
import com.internetstore.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user addresses.
 * Delegates to AddressService and uses MapStructMapper for conversions.
 */
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Addresses", description = "User address management endpoints")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "Create new address")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressRequest request,
                                                         @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(addressService.createAddress(request, userId));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get all addresses for a user")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(@PathVariable String userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @GetMapping("/{userId}/default")
    @Operation(summary = "Get default address for a user")
    public ResponseEntity<AddressResponse> getDefaultAddress(@PathVariable String userId) {
        return ResponseEntity.ok(addressService.getDefaultAddress(userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address by ID")
    public ResponseEntity<Void> deleteAddress(@PathVariable String id, @PathVariable String userId) {
        addressService.deleteAddress(id, userId);
        return ResponseEntity.noContent().build();
    }
}