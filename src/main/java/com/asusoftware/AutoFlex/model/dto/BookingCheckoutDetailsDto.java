package com.asusoftware.AutoFlex.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCheckoutDetailsDto {
    private UUID userId;
    private UUID ownerId;
    private String ownerStripeAccountId;
    private long amountCents;
    private long feeCents;
}