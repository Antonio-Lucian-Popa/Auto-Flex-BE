package com.asusoftware.AutoFlex.payment.repository;

import com.asusoftware.AutoFlex.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByStripePaymentIntentId(String paymentIntentId);
    Optional<Payment> findByStripeCheckoutSessionId(String checkoutSessionId);
}
