package com.asusoftware.AutoFlex.controller;

import com.asusoftware.AutoFlex.model.FuelType;
import com.asusoftware.AutoFlex.model.Transmission;
import com.asusoftware.AutoFlex.model.dto.request.CarRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.CarResponseDto;
import com.asusoftware.AutoFlex.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    /**
     * Creates a new car entry in the database and saves the associated images.
     *
     * @param dto      The car request DTO containing car details.
     * @param images   A list of images to be associated with the car.
     * @param jwt      The JWT token of the authenticated user.
     * @return The created car response DTO.
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<CarResponseDto> createCar(@RequestPart("car") CarRequestDto dto,
                                                    @RequestPart("images") List<MultipartFile> images,
                                                    @AuthenticationPrincipal Jwt jwt) {
        try {
            UUID ownerId = UUID.fromString(jwt.getSubject());
            CarResponseDto response = carService.createCar(dto, ownerId, images);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<CarResponseDto>> getAll() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping
    public ResponseEntity<Page<CarResponseDto>> getFilteredCars(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Transmission transmission,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CarResponseDto> cars = carService.filterCars(search, location, transmission, fuelType, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<CarResponseDto> update(@PathVariable UUID id, @RequestBody CarRequestDto dto) {
        return ResponseEntity.ok(carService.updateCar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
