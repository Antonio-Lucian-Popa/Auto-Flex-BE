package com.asusoftware.AutoFlex.payment.service;

import com.asusoftware.AutoFlex.model.BookingStatus;
import com.asusoftware.AutoFlex.model.User;
import com.asusoftware.AutoFlex.payment.model.Payment;
import com.asusoftware.AutoFlex.payment.repository.PaymentRepository;
import com.asusoftware.AutoFlex.repository.BookingRepository;
import com.asusoftware.AutoFlex.repository.UserRepository;
import com.stripe.Stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @Value("${app.stripe.return-url}")
    private String stripeReturnUrl;

    @Value("${app.stripe.refresh-url}")
    private String stripeRefreshUrl;

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String createCheckoutSession(UUID userId, UUID ownerId, UUID bookingId, long amountCents, long feeCents, String successUrl, String cancelUrl, String ownerStripeAccountId) throws StripeException {

        User owner = userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("Owner not found"));

        if (owner.getStripeAccountId() == null || !Boolean.TRUE.equals(owner.getStripeChargesEnabled()) || !Boolean.TRUE.equals(owner.getStripePayoutsEnabled())) {
            throw new IllegalStateException("Ownerul nu are cont Stripe activ sau complet verificat");
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("eur")
                                        .setUnitAmount(amountCents)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Rezervare masina")
                                                        .build())
                                        .build())
                        .build())
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .setApplicationFeeAmount(feeCents)
                                .setTransferData(
                                        SessionCreateParams.PaymentIntentData.TransferData.builder()
                                                .setDestination(owner.getStripeAccountId())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setOwnerId(ownerId);
        payment.setBookingId(bookingId);
        payment.setStripeCheckoutSessionId(session.getId());
        payment.setStripePaymentIntentId(session.getPaymentIntent());
        payment.setAmount(BigDecimal.valueOf(amountCents).divide(BigDecimal.valueOf(100)));
        payment.setApplicationFee(BigDecimal.valueOf(feeCents).divide(BigDecimal.valueOf(100)));
        payment.setCurrency("EUR");
        payment.setStatus("PENDING");
        payment.setCreatedAt(Instant.now());

        paymentRepository.save(payment);

        return session.getUrl();
    }

    public void handleStripeWebhook(String payload, String sigHeader) throws StripeException {
        Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                paymentRepository.findByStripeCheckoutSessionId(session.getId()).ifPresent(payment -> {
                    payment.setStatus("SUCCEEDED");
                    payment.setUpdatedAt(Instant.now());
                    paymentRepository.save(payment);

                    if (payment.getBookingId() != null) {
                        bookingRepository.findById(payment.getBookingId()).ifPresent(booking -> {
                            booking.setStatus(BookingStatus.CONFIRMED);
                            booking.setUpdatedAt(LocalDateTime.from(Instant.now()));
                            bookingRepository.save(booking);
                        });
                    }
                });
            }
        } else if ("payment_intent.payment_failed".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (intent != null) {
                paymentRepository.findByStripePaymentIntentId(intent.getId()).ifPresent(payment -> {
                    payment.setStatus("FAILED");
                    payment.setUpdatedAt(Instant.now());
                    paymentRepository.save(payment);
                });
            }
        }
    }

    public Map<String, Object> getStripeStatusForOwner(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Map<String, Object> status = new HashMap<>();
        status.put("hasStripeAccount", owner.getStripeAccountId() != null);
        status.put("chargesEnabled", Boolean.TRUE.equals(owner.getStripeChargesEnabled()));
        status.put("payoutsEnabled", Boolean.TRUE.equals(owner.getStripePayoutsEnabled()));

        return status;
    }

    public String createOnboardingLink(UUID ownerId) throws StripeException {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (owner.getStripeAccountId() == null) {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setCountry("RO")
                    .setEmail(owner.getEmail())
                    .build();
            Account account = Account.create(params);
            owner.setStripeAccountId(account.getId());
            userRepository.save(owner);
        }

        AccountLink link = AccountLink.create(
                AccountLinkCreateParams.builder()
                        .setAccount(owner.getStripeAccountId())
                        .setRefreshUrl(stripeRefreshUrl)
                        .setReturnUrl(stripeReturnUrl)
                        .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                        .build()
        );

        return link.getUrl();
    }
}
