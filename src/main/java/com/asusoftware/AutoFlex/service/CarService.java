package com.asusoftware.AutoFlex.service;

import com.asusoftware.AutoFlex.model.dto.request.CarRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.CarResponseDto;

import java.util.List;
import java.util.UUID;

public interface CarService {
    CarResponseDto createCar(CarRequestDto dto, UUID ownerId);
    List<CarResponseDto> getAllCars();
    CarResponseDto getCarById(UUID id);
    CarResponseDto updateCar(UUID id, CarRequestDto dto);
    void deleteCar(UUID id);
}