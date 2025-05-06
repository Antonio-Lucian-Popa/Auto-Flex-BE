package com.asusoftware.AutoFlex.payment.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId; // Clientul

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "booking_id")
    private UUID bookingId;

    @Column(name = "stripe_payment_intent_id", nullable = false)
    private String stripePaymentIntentId;

    @Column(name = "stripe_checkout_session_id")
    private String stripeCheckoutSessionId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "application_fee", nullable = false)
    private BigDecimal applicationFee;

    @Column(nullable = false)
    private String currency = "EUR";

    @Column(nullable = false)
    private String status; // PENDING, SUCCEEDED, FAILED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

}
