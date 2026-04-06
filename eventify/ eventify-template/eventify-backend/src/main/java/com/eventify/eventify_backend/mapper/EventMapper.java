package com.eventify.eventify_backend.mapper;

import com.eventify.eventify_backend.api.dto.request.EventCreateRequest;
import com.eventify.eventify_backend.api.dto.request.EventUpdateRequest;
import com.eventify.eventify_backend.api.dto.response.EventResponse;
import com.eventify.eventify_backend.domain.model.Event;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EventMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "availableTickets", source = "totalTickets")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    Event toEntity(EventCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "availableTickets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Event event, EventUpdateRequest request);

    EventResponse toResponse(Event event);
}