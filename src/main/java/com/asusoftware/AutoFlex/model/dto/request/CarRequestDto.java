package com.asusoftware.AutoFlex.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CarRequestDto {
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
}
