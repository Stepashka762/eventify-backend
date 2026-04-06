
package com.eventify.eventify_backend.mapper;

import com.eventify.eventify_backend.api.dto.request.RegisterRequest;
import com.eventify.eventify_backend.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "telegramChatId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    User toEntity(RegisterRequest request);
}