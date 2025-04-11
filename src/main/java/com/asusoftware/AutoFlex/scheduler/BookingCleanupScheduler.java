package com.asusoftware.AutoFlex.scheduler;

import com.asusoftware.AutoFlex.model.Booking;
import com.asusoftware.AutoFlex.model.BookingStatus;
import com.asusoftware.AutoFlex.model.CarStatus;
import com.asusoftware.AutoFlex.repository.BookingRepository;
import com.asusoftware.AutoFlex.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;

    @Scheduled(cron = "0 0 * * * *") // la fiecare oră fix
    public void cleanupOldBookings() {
        LocalDate today = LocalDate.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndEndDateBefore(BookingStatus.CONFIRMED, today);

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.COMPLETED);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);

            carRepository.findById(booking.getCarId()).ifPresent(car -> {
                car.setCarStatus(CarStatus.AVAILABLE);
                car.setUpdatedAt(LocalDateTime.now());
                carRepository.save(car);
            });

            log.info("Auto-completed booking {} and made car {} AVAILABLE", booking.getId(), booking.getCarId());
        }
    }
}
