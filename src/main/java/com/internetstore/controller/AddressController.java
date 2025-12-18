package com.internetstore.controller;

import com.internetstore.dto.request.AddressRequest;
import com.internetstore.dto.response.AddressResponse;
import com.internetstore.entity.User;
import com.internetstore.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Addresses", description = "User address management endpoints")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "Create a new address")
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal User user) {

        AddressResponse response =
                addressService.createAddress(request, user.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all user addresses")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                addressService.getUserAddresses(user.getId())
        );
    }

    @GetMapping("/default")
    @Operation(summary = "Get default address")
    public ResponseEntity<AddressResponse> getDefaultAddress(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                addressService.getDefaultAddress(user.getId())
        );
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete address by ID")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable String addressId,
            @AuthenticationPrincipal User user) {

        addressService.deleteAddress(addressId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
