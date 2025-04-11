package com.asusoftware.AutoFlex.service.impl;

import com.asusoftware.AutoFlex.model.*;
import com.asusoftware.AutoFlex.model.dto.request.BookingRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingCarDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingIntervalDto;
import com.asusoftware.AutoFlex.model.dto.response.BookingResponseDto;
import com.asusoftware.AutoFlex.model.dto.response.CarResponseDto;
import com.asusoftware.AutoFlex.repository.BookingRepository;
import com.asusoftware.AutoFlex.repository.CarRepository;
import com.asusoftware.AutoFlex.repository.UserRepository;
import com.asusoftware.AutoFlex.service.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public BookingServiceImpl(BookingRepository bookingRepository, ModelMapper mapper, CarRepository carRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
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
        // find user by keycloakId
        User client = userRepository.findByKeycloakId(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));
        booking.setClientId(client.getId());
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setTotalPrice(calculatePrice(dto));

        return mapper.map(bookingRepository.save(booking), BookingResponseDto.class);
    }


    private BigDecimal calculatePrice(BookingRequestDto dto) {
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new NoSuchElementException("Car not found"));

        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        return car.getPrice().multiply(BigDecimal.valueOf(days));
    }


    @Override
    public List<BookingCarDto> getBookingsByUser(UUID clientKeycloakId) {
        User client = userRepository.findByKeycloakId(clientKeycloakId)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));

        // Obținem toate rezervările userului
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(b -> b.getClientId().equals(client.getId()))
                .toList();

        return bookings.stream()
                .map(booking -> {
                    Car car = carRepository.findById(booking.getCarId())
                            .orElseThrow(() -> new NoSuchElementException("Car not found"));

                    BookingCarDto carResponseDto = mapper.map(car, BookingCarDto.class);
                    carResponseDto.setStartDate(booking.getStartDate());
                    carResponseDto.setEndDate(booking.getEndDate());
                    carResponseDto.setTotalPrice(booking.getTotalPrice());

//                    BookingCarDto dto = new BookingCarDto();
//                    dto.setId(booking.getId());
//                    dto.setStartDate(booking.getStartDate());
//                    dto.setEndDate(booking.getEndDate());
//                    dto.setTotalPrice(booking.getTotalPrice());
//                    dto.setStatus(String.valueOf(booking.getStatus()));
//                    dto.setCreatedAt(car.getCreatedAt());
//                    dto.setUpdatedAt(car.getUpdatedAt());
//
//                    dto.setBrand(car.getBrand());
//                    dto.setModel(car.getModel());
//                    dto.setYear(car.getYear());
//                    dto.setTransmission(car.getTransmission().name());
//                    dto.setFuelType(car.getFuelType().name());
//                    dto.setPrice(car.getPrice());
//                    dto.setLocation(car.getLocation());
//                    dto.setDescription(car.getDescription());
//                    dto.setFeatures(car.getFeatures());
//                    dto.setImages(car.getImages());
//                    dto.setRating(car.getRating());

                    return carResponseDto;
                })
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
        User client = userRepository.findByKeycloakId(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));
        return carRepository.findById(carId)
                .map(car -> car.getOwnerId().equals(client.getId()))
                .orElse(false);
    }

}