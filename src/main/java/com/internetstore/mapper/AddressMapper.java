package com.internetstore.mapper;

import com.internetstore.dto.request.AddressRequest;
import com.internetstore.dto.response.AddressResponse;
import com.internetstore.entity.Address;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AddressMapper {

    // -------------------------------
    // ADDRESS MAPPINGS
    // -------------------------------

    @Mapping(target = "isDefault", source = "isDefault", defaultValue = "false")
    Address fromRequest(AddressRequest request);

    AddressResponse toResponse(Address address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Address fromResponse(AddressResponse response);

    // -------------------------------
    // LIST MAPPINGS
    // -------------------------------

    List<AddressResponse> toResponseList(List<Address> addresses);
}
