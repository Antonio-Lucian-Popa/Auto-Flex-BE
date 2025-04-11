package com.asusoftware.AutoFlex.service.impl;

import com.asusoftware.AutoFlex.model.Booking;
import com.asusoftware.AutoFlex.model.BookingStatus;
import com.asusoftware.AutoFlex.model.CarStatus;
import com.asusoftware.AutoFlex.model.dto.request.BookingRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingIntervalDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingResponseDto;
import com.asusoftware.AutoFlex.repository.BookingRepository;
import com.asusoftware.AutoFlex.repository.CarRepository;
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
    private final CarRepository carRepository;
    private final ModelMapper mapper;

    public BookingServiceImpl(BookingRepository bookingRepository, ModelMapper mapper, CarRepository carRepository) {
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
        this.carRepository = carRepository;
    }

    @Override
    public BookingResponseDto createBooking(UUID clientId, BookingRequestDto dto) {
        if (userOwnsCar(clientId, dto.getCarId())) {
            throw new IllegalArgumentException("You cannot book your own car.");
        }

        // Verificăm suprapunerea
        boolean hasOverlap = bookingRepository.existsByCarIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                dto.getCarId(),
                List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING),
                dto.getEndDate(),
                dto.getStartDate()
        );

        if (hasOverlap) {
            throw new IllegalArgumentException("Selected dates are already booked.");
        }

        // continuăm cu logica de creare booking
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setCarId(dto.getCarId());
        booking.setClientId(clientId);
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        return mapper.map(bookingRepository.save(booking), BookingResponseDto.class);
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
    public List<BookingIntervalDto> getOccupiedIntervalsForCar(UUID carId) {
        List<Booking> bookings = bookingRepository.findByCarIdAndStatusIn(
                carId, List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING)
        );

        return bookings.stream()
                .map(b -> new BookingIntervalDto(b.getStartDate(), b.getEndDate()))
                .toList();
    }


    @Override
    public BookingResponseDto updateBookingStatus(UUID bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        BookingStatus newStatus = BookingStatus.valueOf(status);
        booking.setStatus(BookingStatus.valueOf(status));
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Actualizare status mașină dacă statusul este CONFIRMED

            carRepository.findById(booking.getCarId()).ifPresent(car -> {
                switch (newStatus) {
                    case CONFIRMED -> car.setCarStatus(CarStatus.BOOKED);
                    case CANCELLED, COMPLETED -> car.setCarStatus(CarStatus.AVAILABLE);
                }
                car.setUpdatedAt(LocalDateTime.now());
                carRepository.save(car);
            });

        return mapper.map(booking, BookingResponseDto.class);
    }

    private boolean userOwnsCar(UUID clientId, UUID carId) {
        return carRepository.findById(carId)
                .map(car -> car.getOwnerId().equals(clientId))
                .orElse(false);
    }

}