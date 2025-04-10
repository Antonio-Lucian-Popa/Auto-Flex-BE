package com.asusoftware.AutoFlex.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReviewRequestDto {
    private UUID bookingId;
    private UUID carId;
    private int rating;
    private String comment;
}

