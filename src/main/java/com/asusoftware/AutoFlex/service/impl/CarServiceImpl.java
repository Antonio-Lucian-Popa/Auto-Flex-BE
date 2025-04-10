package com.asusoftware.AutoFlex.service.impl;

import com.asusoftware.AutoFlex.model.Car;
import com.asusoftware.AutoFlex.model.CarStatus;
import com.asusoftware.AutoFlex.model.dto.request.CarRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.CarResponseDto;
import com.asusoftware.AutoFlex.repository.CarRepository;
import com.asusoftware.AutoFlex.service.CarService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.io.File;


@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final ModelMapper mapper;
    private final Path baseStoragePath = Paths.get("uploads/images");

    public CarServiceImpl(CarRepository carRepository, ModelMapper mapper) throws IOException {
        this.carRepository = carRepository;
        this.mapper = mapper;
        Files.createDirectories(baseStoragePath);
    }

    @Override
    public CarResponseDto createCar(CarRequestDto dto, UUID ownerId) {
        UUID carId = UUID.randomUUID();
        Car car = mapper.map(dto, Car.class);
        car.setId(carId);
        car.setOwnerId(ownerId);
        car.setCarStatus(CarStatus.AVAILABLE);
        car.setCreatedAt(LocalDateTime.now());
        car.setUpdatedAt(LocalDateTime.now());
        carRepository.save(car);

        try {
            Files.createDirectories(baseStoragePath.resolve(carId.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create image folder for car: " + carId);
        }

        return mapper.map(car, CarResponseDto.class);
    }

    public String saveImage(UUID carId, MultipartFile file) {
        try {
            if (file.isEmpty()) throw new IllegalArgumentException("Uploaded file is empty");
            Path carFolder = baseStoragePath.resolve(carId.toString());
            Files.createDirectories(carFolder);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = carFolder.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/images/" + carId + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image: " + e.getMessage());
        }
    }

    @Override
    public List<CarResponseDto> getAllCars() {
        return carRepository.findAll().stream()
                .map(car -> mapper.map(car, CarResponseDto.class))
                .toList();
    }

    @Override
    public CarResponseDto getCarById(UUID id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Car with ID " + id + " not found"));
        return mapper.map(car, CarResponseDto.class);
    }

    @Override
    public CarResponseDto updateCar(UUID id, CarRequestDto dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Car with ID " + id + " not found"));
        mapper.map(dto, car);
        car.setUpdatedAt(LocalDateTime.now());
        return mapper.map(carRepository.save(car), CarResponseDto.class);
    }

    @Override
    public void deleteCar(UUID id) {
        Path carFolder = baseStoragePath.resolve(id.toString());
        try {
            if (Files.exists(carFolder)) {
                Files.walk(carFolder)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image folder for car: " + id);
        }

        if (!carRepository.existsById(id)) {
            throw new NoSuchElementException("Car with ID " + id + " not found");
        }
        carRepository.deleteById(id);
    }
}