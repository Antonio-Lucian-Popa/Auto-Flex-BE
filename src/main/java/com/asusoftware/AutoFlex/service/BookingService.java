package com.asusoftware.AutoFlex.service;

import com.asusoftware.AutoFlex.model.dto.request.BookingRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingResponseDto;

import java.util.List;
import java.util.UUID;
/**
 * BookingService interface for managing car bookings.
 * It provides methods to create, retrieve, and update bookings.
 */
public interface BookingService {
    BookingResponseDto createBooking(UUID clientId, BookingRequestDto dto);
    List<BookingResponseDto> getBookingsByUser(UUID clientId);
    List<BookingResponseDto> getBookingsByCar(UUID carId);
    BookingResponseDto updateBookingStatus(UUID bookingId, String status);
}
