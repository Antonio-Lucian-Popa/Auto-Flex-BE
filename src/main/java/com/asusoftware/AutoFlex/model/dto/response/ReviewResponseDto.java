package com.asusoftware.AutoFlex.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReviewResponseDto {
    private UUID id;
    private UUID bookingId;
    private UUID carId;
    private UUID clientId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
