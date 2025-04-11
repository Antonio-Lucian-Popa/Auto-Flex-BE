package com.asusoftware.AutoFlex.service.impl;

import com.asusoftware.AutoFlex.model.Car;
import com.asusoftware.AutoFlex.model.CarStatus;
import com.asusoftware.AutoFlex.model.dto.request.CarRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.CarResponseDto;
import com.asusoftware.AutoFlex.repository.CarRepository;
import com.asusoftware.AutoFlex.service.CarService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.io.File;


@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final ModelMapper mapper;
    private final Path baseStoragePath = Paths.get("uploads/images");

    @Value("${external-link.url}")
    private String externalLinkBase;

    public CarServiceImpl(CarRepository carRepository, ModelMapper mapper) throws IOException {
        this.carRepository = carRepository;
        this.mapper = mapper;
        Files.createDirectories(baseStoragePath);
    }

    /**
     * Creates a new car entry in the database and saves the associated images.
     *
     * @param dto      The car request DTO containing car details.
     * @param ownerId  The ID of the owner of the car.
     * @param images   A list of images to be associated with the car.
     * @return The created car response DTO.
     */
    @Override
    public CarResponseDto createCar(CarRequestDto dto, UUID ownerId, List<MultipartFile> images) {
        UUID carId = UUID.randomUUID();
        Car car = mapper.map(dto, Car.class);
        car.setId(carId);
        car.setOwnerId(ownerId);
        car.setCarStatus(CarStatus.AVAILABLE);
        car.setCreatedAt(LocalDateTime.now());
        car.setUpdatedAt(LocalDateTime.now());

        // Save images and collect their URLs
        List<String> imagePaths = images.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> saveImage(file, carId))
                .toList();

        car.setImages(imagePaths);
        carRepository.save(car);
        return mapper.map(car, CarResponseDto.class);
    }

    private String saveImage(MultipartFile file, UUID carId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file.");
        }
        try {
            String originalFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path directory = Paths.get("uploads/images", carId.toString()).toAbsolutePath().normalize();
            Files.createDirectories(directory);

            Path destination = directory.resolve(originalFilename).normalize();

            if (!destination.getParent().equals(directory)) {
                throw new SecurityException("Attempt to store file outside allowed directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            return externalLinkBase + carId + "/" + originalFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
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