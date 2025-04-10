package com.asusoftware.AutoFlex.service.impl;

import com.asusoftware.AutoFlex.model.Booking;
import com.asusoftware.AutoFlex.model.BookingStatus;
import com.asusoftware.AutoFlex.model.dto.request.BookingRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingResponseDto;
import com.asusoftware.AutoFlex.repository.BookingRepository;
import com.asusoftware.AutoFlex.service.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ModelMapper mapper;

    public BookingServiceImpl(BookingRepository bookingRepository, ModelMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
    }

    @Override
    public BookingResponseDto createBooking(UUID clientId, BookingRequestDto dto) {
        Booking booking = mapper.map(dto, Booking.class);
        booking.setId(UUID.randomUUID());
        booking.setClientId(clientId);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setTotalPrice(calculatePrice(dto));
        bookingRepository.save(booking);
        return mapper.map(booking, BookingResponseDto.class);
    }

    private BigDecimal calculatePrice(BookingRequestDto dto) {
        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        // This should be fetched from the CarService, for now just mocked
        BigDecimal dailyRate = BigDecimal.valueOf(100);
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(UUID clientId) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getClientId().equals(clientId))
                .map(b -> mapper.map(b, BookingResponseDto.class))
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByCar(UUID carId) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getCarId().equals(carId))
                .map(b -> mapper.map(b, BookingResponseDto.class))
                .toList();
    }

    @Override
    public BookingResponseDto updateBookingStatus(UUID bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.setStatus(BookingStatus.valueOf(status));
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return mapper.map(booking, BookingResponseDto.class);
    }
}