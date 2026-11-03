package com.careerbridge.payment.client;

import com.careerbridge.payment.entity.PaymentApproval;
import com.careerbridge.payment.entity.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class TossPaymentClientTest {
    private MockRestServiceServer server;
    private TossPaymentClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new TossPaymentClient(builder, "https://payments.example.test", "test-secret");
    }

    @Test
    void confirmsPaymentAndMapsApproval() {
        server.expect(requestTo("https://payments.example.test/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic dGVzdC1zZWNyZXQ6"))
                .andExpect(header("Idempotency-Key", "order-1"))
                .andExpect(content().json("""
                        {"paymentKey":"key-1","orderId":"order-1","amount":50000}
                        """))
                .andRespond(withSuccess(response("key-1", "order-1", 50000, "DONE"), MediaType.APPLICATION_JSON));

        PaymentApproval approval = client.confirm("key-1", "order-1", 50000);

        assertThat(approval.paymentKey()).isEqualTo("key-1");
        assertThat(approval.paymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(approval.approvedAt()).isEqualTo(LocalDateTime.of(2026, 10, 30, 12, 0));
        server.verify();
    }

    @ParameterizedTest
    @CsvSource({"wrong-key,order-1,50000,DONE", "key-1,wrong-order,50000,DONE",
            "key-1,order-1,10000,DONE", "key-1,order-1,50000,WAITING_FOR_DEPOSIT"})
    void rejectsUnverifiedApproval(String key, String order, int amount, String status) {
        server.expect(requestTo("https://payments.example.test/v1/payments/confirm"))
                .andRespond(withSuccess(response(key, order, amount, status), MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> client.confirm("key-1", "order-1", 50000))
                .isInstanceOf(IllegalStateException.class);
        server.verify();
    }

    @Test
    void propagatesGatewayRejection() {
        server.expect(requestTo("https://payments.example.test/v1/payments/confirm"))
                .andRespond(withBadRequest());
        assertThatThrownBy(() -> client.confirm("key-1", "order-1", 50000))
                .isInstanceOf(IllegalStateException.class);
        server.verify();
    }

    @Test
    void rejectsMissingSecretBeforeHttpRequest() {
        client = new TossPaymentClient(RestClient.builder(), "https://payments.example.test", "");
        assertThatThrownBy(() -> client.confirm("key-1", "order-1", 50000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("secret key");
    }

    private String response(String key, String order, int amount, String status) {
        return """
                {"paymentKey":"%s","orderId":"%s","totalAmount":%d,"status":"%s",
                 "method":"\uCE74\uB4DC","approvedAt":"2026-10-30T12:00:00+09:00"}
                """.formatted(key, order, amount, status);
    }
}
