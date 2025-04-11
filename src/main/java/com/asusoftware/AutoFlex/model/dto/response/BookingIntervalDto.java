package com.asusoftware.AutoFlex.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingIntervalDto {
    private LocalDate startDate;
    private LocalDate endDate;
}
