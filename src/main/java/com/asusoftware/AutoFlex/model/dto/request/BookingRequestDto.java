package com.asusoftware.AutoFlex.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class BookingRequestDto {
    private UUID carId;
    private LocalDate startDate;
    private LocalDate endDate;
}