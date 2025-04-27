package com.asusoftware.AutoFlex.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "cars")
public class Car {
    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    private String brand;
    private String model;
    private int year;
    private int power;
    private int seats;

    @Enumerated(EnumType.STRING)
    private Transmission transmission;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private BigDecimal price;
    private String location;
    private String description;

    @ElementCollection
    @CollectionTable(name = "car_features", joinColumns = @JoinColumn(name = "car_id"))
    private List<String> features;

    @ElementCollection
    @CollectionTable(name = "car_images", joinColumns = @JoinColumn(name = "car_id"))
    private List<String> images;

    @Enumerated(EnumType.STRING)
    private CarStatus carStatus;

    private Double rating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
