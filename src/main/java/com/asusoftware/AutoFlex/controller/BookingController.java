package com.asusoftware.AutoFlex.controller;

import com.asusoftware.AutoFlex.model.dto.request.BookingRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingCarDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingIntervalDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingResponseDto;
import com.asusoftware.AutoFlex.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BookingResponseDto> create(@RequestBody BookingRequestDto dto,
                                                     @AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(clientId, dto));
    }

    @GetMapping("/user")
    public ResponseEntity<List<BookingCarDto>> getForUser(@AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(bookingService.getBookingsByUser(clientId));
    }

    @GetMapping("/car/{carId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<BookingResponseDto>> getForCar(@PathVariable UUID carId) {
        return ResponseEntity.ok(bookingService.getBookingsByCar(carId));
    }

    /**
     * Get all occupied dates for a car, is used to check if a car is available for booking.
     * @param carId
     * @return
     */
    @GetMapping("/car/{carId}/occupied")
    public ResponseEntity<List<BookingIntervalDto>> getOccupiedDates(@PathVariable UUID carId) {
        return ResponseEntity.ok(bookingService.getOccupiedIntervalsForCar(carId));
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponseDto> updateStatus(@PathVariable UUID id,
                                                           @RequestParam String status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }
}

