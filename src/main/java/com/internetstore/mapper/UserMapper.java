package com.internetstore.mapper;

import com.internetstore.dto.request.RegisterRequest;
import com.internetstore.dto.response.UserResponse;
import com.internetstore.entity.User;
import com.internetstore.enums.UserRole;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    // -------------------------------
    // USER MAPPINGS
    // -------------------------------

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "addressIds", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User fromRegisterRequest(RegisterRequest request);

    // -------------------------------
    // HELPERS
    // -------------------------------

    default List<String> mapRoles(List<UserRole> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Enum::name)
                .toList();
    }
}
