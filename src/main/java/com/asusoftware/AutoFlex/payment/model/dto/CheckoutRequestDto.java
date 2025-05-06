package com.asusoftware.AutoFlex.payment.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CheckoutRequestDto {
    private UUID userId;
    private UUID ownerId;
    private UUID bookingId;
    private long amountCents;
    private long feeCents;
    private String successUrl;
    private String cancelUrl;
    private String ownerStripeAccountId;
}