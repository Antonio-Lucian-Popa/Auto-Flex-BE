package com.asusoftware.AutoFlex.payment.controller;

import com.asusoftware.AutoFlex.payment.model.dto.CheckoutRequestDto;
import com.asusoftware.AutoFlex.payment.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.financialconnections.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createCheckout(@RequestBody CheckoutRequestDto request) {
        try {
            String sessionUrl = stripeService.createCheckoutSession(
                    request.getUserId(),
                    request.getOwnerId(),
                    request.getBookingId(),
                    request.getAmountCents(),
                    request.getFeeCents(),
                    request.getSuccessUrl(),
                    request.getCancelUrl(),
                    request.getOwnerStripeAccountId()
            );
            return ResponseEntity.ok(Map.of("url", sessionUrl));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            String payload = reader.lines().collect(Collectors.joining());
            String sigHeader = request.getHeader("Stripe-Signature");
            stripeService.handleStripeWebhook(payload, sigHeader);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error: " + e.getMessage());
        }
    }

    @GetMapping("/owner-status/{ownerId}")
    public ResponseEntity<Map<String, Object>> getOwnerStripeStatus(@PathVariable UUID ownerId) {
        Map<String, Object> status = stripeService.getStripeStatusForOwner(ownerId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/onboarding-link/{ownerId}")
    public ResponseEntity<Map<String, String>> createOnboardingLink(@PathVariable UUID ownerId) {
        try {
            String url = stripeService.createOnboardingLink(ownerId);
            return ResponseEntity.ok(Map.of("onboardingUrl", url));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}

