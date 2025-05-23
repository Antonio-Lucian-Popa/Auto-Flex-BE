package com.asusoftware.AutoFlex.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CarRequestDto {
    private String brand;
    private String model;
    private int year;
    private int power;
    private int seats;
    private String transmission;
    private String fuelType;
    private BigDecimal price;
    private String location;
    private String description;
    private List<String> features;
    private List<String> images;
    private UUID ownerId;
}
