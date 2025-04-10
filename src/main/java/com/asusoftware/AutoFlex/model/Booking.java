package com.asusoftware.AutoFlex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private UUID id;

    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
