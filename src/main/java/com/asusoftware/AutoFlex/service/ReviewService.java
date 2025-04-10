package com.asusoftware.AutoFlex.service;

import com.asusoftware.AutoFlex.model.dto.request.ReviewRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.ReviewResponseDto;

import java.util.List;
import java.util.UUID;

/**
 * ReviewService interface for review-related operations.
 */
public interface ReviewService {
    ReviewResponseDto createReview(UUID clientId, ReviewRequestDto dto);
    List<ReviewResponseDto> getReviewsByCar(UUID carId);
    List<ReviewResponseDto> getReviewsByUser(UUID clientId);
}
