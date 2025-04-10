package com.asusoftware.AutoFlex.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    private int rating; // 1 to 5
    private String comment;

    private LocalDateTime createdAt;
}

