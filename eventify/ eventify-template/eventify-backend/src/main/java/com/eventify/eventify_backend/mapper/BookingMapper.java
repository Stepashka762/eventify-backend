
package com.eventify.eventify_backend.mapper;

import com.eventify.eventify_backend.api.dto.response.BookingResponse;
import com.eventify.eventify_backend.domain.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface BookingMapper {

    @Mapping(target = "customerEmail", source = "user.email")
    @Mapping(target = "timezone", constant = "UTC")
    @Mapping(target = "status", source = "booking", qualifiedByName = "getStatus")
    BookingResponse toResponse(Booking booking);

    @Named("getStatus")
    default String getStatus(Booking booking) {
        return booking.getStatus();
    }
}