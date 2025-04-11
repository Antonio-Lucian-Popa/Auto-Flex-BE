package com.asusoftware.AutoFlex.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BookingCarDto {
    private UUID id;
    private String brand;
    private String model;
    private int year;
    private String transmission;
    private String fuelType;
    private BigDecimal price;
    private String location;
    private String description;
    private List<String> features;
    private List<String> images;
    private String status;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
}
