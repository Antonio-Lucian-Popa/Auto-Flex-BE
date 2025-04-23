package com.asusoftware.AutoFlex.service;

import com.asusoftware.AutoFlex.model.FuelType;
import com.asusoftware.AutoFlex.model.Transmission;
import com.asusoftware.AutoFlex.model.dto.request.CarRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.CarResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CarService {
    CarResponseDto createCar(CarRequestDto dto, UUID jwtUserId, List<MultipartFile> images);
    List<CarResponseDto> getAllCars();
    Page<CarResponseDto> filterCars(String search, String location, Transmission transmission, FuelType fuelType,
                                    BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    CarResponseDto getCarById(UUID id);
    CarResponseDto updateCar(UUID id, CarRequestDto dto);
    void deleteCar(UUID id, UUID jwtUserId);
}