package com.asusoftware.AutoFlex.repository;

import com.asusoftware.AutoFlex.model.Booking;
import com.asusoftware.AutoFlex.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByStatusAndEndDateBefore(BookingStatus status, LocalDate endDate);
    List<Booking> findByCarIdAndStatusIn(UUID carId, List<BookingStatus> statuses);
    boolean existsByCarIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            UUID carId,
            List<BookingStatus> statuses,
            LocalDate endDate,
            LocalDate startDate
    );

}
