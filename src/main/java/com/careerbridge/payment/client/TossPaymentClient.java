package com.careerbridge.payment.client;

import com.careerbridge.payment.entity.PaymentApproval;
import com.careerbridge.payment.entity.PaymentMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Component
public class TossPaymentClient {

    private static final String DONE_STATUS = "DONE";
    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final String CARD = "\uCE74\uB4DC";
    private static final String TRANSFER = "\uACC4\uC88C\uC774\uCCB4";
    private static final String PHONE = "\uD734\uB300\uD3F0";
    private static final String VIRTUAL_ACCOUNT = "\uAC00\uC0C1\uACC4\uC88C";
    private static final String EASY_PAY = "\uAC04\uD3B8\uACB0\uC81C";
    private static final String CULTURE_GIFT_CERTIFICATE = "\uBB38\uD654\uC0C1\uD488\uAD8C";
    private static final String BOOK_GIFT_CERTIFICATE = "\uB3C4\uC11C\uBB38\uD654\uC0C1\uD488\uAD8C";
    private static final String GAME_GIFT_CERTIFICATE = "\uAC8C\uC784\uBB38\uD654\uC0C1\uD488\uAD8C";

    private final RestClient restClient;
    private final String secretKey;

    public TossPaymentClient(
            RestClient.Builder restClientBuilder,
            @Value("${payment.toss.base-url:https://api.tosspayments.com}") String baseUrl,
            @Value("${payment.toss.secret-key:}") String secretKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.secretKey = secretKey;
    }

    public PaymentApproval confirm(
            String paymentKey,
            String orderId,
            Integer amount
    ) {
        validateSecretKey();

        TossConfirmResponse response = requestConfirm(paymentKey, orderId, amount);
        validateConfirmResponse(response, paymentKey, orderId, amount);

        return new PaymentApproval(
                response.paymentKey(),
                toPaymentMethod(response.method()),
                parseApprovedAt(response.approvedAt())
        );
    }

    private TossConfirmResponse requestConfirm(
            String paymentKey,
            String orderId,
            Integer amount
    ) {
        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .header(IDEMPOTENCY_KEY_HEADER, orderId)
                    .headers(headers -> headers.setBasicAuth(secretKey, ""))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new TossConfirmRequest(paymentKey, orderId, amount))
                    .retrieve()
                    .body(TossConfirmResponse.class);
        } catch (RestClientResponseException exception) {
            throw new IllegalStateException("Toss payment confirm request failed.", exception);
        }
    }

    private void validateSecretKey() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("Toss Payments secret key is not configured.");
        }
    }

    private void validateConfirmResponse(
            TossConfirmResponse response,
            String paymentKey,
            String orderId,
            Integer amount
    ) {
        if (response == null) {
            throw new IllegalStateException("Toss payment confirm response is empty.");
        }

        if (!paymentKey.equals(response.paymentKey())) {
            throw new IllegalStateException("Toss payment key does not match.");
        }

        if (!orderId.equals(response.orderId())) {
            throw new IllegalStateException("Toss order id does not match.");
        }

        if (!amount.equals(response.totalAmount())) {
            throw new IllegalStateException("Toss payment amount does not match.");
        }

        if (!DONE_STATUS.equals(response.status())) {
            throw new IllegalStateException("Toss payment is not approved.");
        }
    }

    private PaymentMethod toPaymentMethod(String method) {
        if (CARD.equals(method)) {
            return PaymentMethod.CARD;
        }

        if (TRANSFER.equals(method)) {
            return PaymentMethod.TRANSFER;
        }

        if (PHONE.equals(method)) {
            return PaymentMethod.PHONE;
        }

        if (VIRTUAL_ACCOUNT.equals(method)) {
            return PaymentMethod.VIRTUAL_ACCOUNT;
        }

        if (EASY_PAY.equals(method)) {
            return PaymentMethod.EASY_PAY;
        }

        if (CULTURE_GIFT_CERTIFICATE.equals(method)
                || BOOK_GIFT_CERTIFICATE.equals(method)
                || GAME_GIFT_CERTIFICATE.equals(method)) {
            return PaymentMethod.GIFT_CERTIFICATE;
        }

        return PaymentMethod.OTHER;
    }

    private LocalDateTime parseApprovedAt(String approvedAt) {
        if (approvedAt == null || approvedAt.isBlank()) {
            throw new IllegalStateException("Toss approved time is empty.");
        }

        return OffsetDateTime.parse(approvedAt).toLocalDateTime();
    }

    private record TossConfirmRequest(
            String paymentKey,
            String orderId,
            Integer amount
    ) {
    }

    private record TossConfirmResponse(
            String paymentKey,
            String orderId,
            String method,
            String status,
            Integer totalAmount,
            String approvedAt
    ) {
    }
}
