package com.asusoftware.AutoFlex.controller;

import com.asusoftware.AutoFlex.model.dto.request.ReviewRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.ReviewResponseDto;
import com.asusoftware.AutoFlex.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReviewResponseDto> create(@RequestBody ReviewRequestDto dto,
                                                    @AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(clientId, dto));
    }

    @GetMapping("/car/{carId}")
    public ResponseEntity<List<ReviewResponseDto>> getForCar(@PathVariable UUID carId) {
        return ResponseEntity.ok(reviewService.getReviewsByCar(carId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReviewResponseDto>> getForUser(@AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(reviewService.getReviewsByUser(clientId));
    }
}
