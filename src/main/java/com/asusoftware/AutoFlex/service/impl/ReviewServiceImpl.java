package com.asusoftware.AutoFlex.service.impl;

import com.asusoftware.AutoFlex.model.Review;
import com.asusoftware.AutoFlex.model.dto.request.ReviewRequestDto;
import com.asusoftware.AutoFlex.model.dto.response.ReviewResponseDto;
import com.asusoftware.AutoFlex.repository.ReviewRepository;
import com.asusoftware.AutoFlex.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ModelMapper mapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ModelMapper mapper) {
        this.reviewRepository = reviewRepository;
        this.mapper = mapper;
    }

    @Override
    public ReviewResponseDto createReview(UUID clientId, ReviewRequestDto dto) {
        Review review = mapper.map(dto, Review.class);
        review.setId(UUID.randomUUID());
        review.setClientId(clientId);
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        return mapper.map(review, ReviewResponseDto.class);
    }

    @Override
    public List<ReviewResponseDto> getReviewsByCar(UUID carId) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getCarId().equals(carId))
                .map(r -> mapper.map(r, ReviewResponseDto.class))
                .toList();
    }

    @Override
    public List<ReviewResponseDto> getReviewsByUser(UUID clientId) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getClientId().equals(clientId))
                .map(r -> mapper.map(r, ReviewResponseDto.class))
                .toList();
    }
}
