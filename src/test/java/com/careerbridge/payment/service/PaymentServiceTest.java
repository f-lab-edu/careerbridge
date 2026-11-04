package com.careerbridge.payment.service;

import com.careerbridge.match.domain.Match;
import com.careerbridge.match.domain.MatchStatus;
import com.careerbridge.match.repository.MatchRepository;
import com.careerbridge.payment.client.TossPaymentClient;
import com.careerbridge.payment.entity.*;
import com.careerbridge.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TossPaymentClient tossPaymentClient;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
                paymentRepository, matchRepository, tossPaymentClient
        );
    }

    @Test
    void 승인에_성공하면_결제와_매칭이_PAID가_된다() {
        Match match = approvedMatch();
        Payment payment = Payment.ready(
                match, 50_000, PaymentProvider.TOSS, "order-1"
        );
        LocalDateTime approvedAt = LocalDateTime.of(2026, 11, 3, 12, 0);

        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail(
                "order-1", "mentee@example.com"
        )).thenReturn(Optional.of(payment));

        when(tossPaymentClient.confirm("key-1", "order-1", 50_000))
                .thenReturn(new PaymentApproval(
                        "key-1", PaymentMethod.CARD, approvedAt
                ));

        Payment result = paymentService.confirm(
                "mentee@example.com", "order-1", "key-1", 50_000
        );

        assertThat(result).isSameAs(payment);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getPaymentKey()).isEqualTo("key-1");
        assertThat(payment.getPaidAt()).isEqualTo(approvedAt);
        assertThat(match.getMatchStatus()).isEqualTo(MatchStatus.PAID);
        assertThat(match.getPaidAt()).isEqualTo(approvedAt);
    }

    @Test
    void PG_승인이_실패하면_결제와_매칭_상태를_유지한다() {
        Match match = approvedMatch();
        Payment payment = Payment.ready(
                match, 50_000, PaymentProvider.TOSS, "order-1"
        );

        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail(
                "order-1", "mentee@example.com"
        )).thenReturn(Optional.of(payment));

        when(tossPaymentClient.confirm("key-1", "order-1", 50_000))
                .thenThrow(new IllegalStateException("PG approval failed"));

        assertThatThrownBy(() -> paymentService.confirm(
                "mentee@example.com", "order-1", "key-1", 50_000
        )).isInstanceOf(IllegalStateException.class);

        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(payment.getPaymentKey()).isNull();
        assertThat(payment.getPaidAt()).isNull();
        assertThat(match.getMatchStatus()).isEqualTo(MatchStatus.APPROVED);
        assertThat(match.getPaidAt()).isNull();
    }

    @Test
    void repeatedConfirmationReturnsExistingPaymentWithoutGatewayCall() {
        Match match = approvedMatch();
        Payment payment = Payment.ready(match, 50_000, PaymentProvider.TOSS, "order-1");
        payment.complete("key-1", PaymentMethod.CARD, LocalDateTime.of(2026, 11, 3, 12, 0));
        match.pay(payment.getPaidAt());
        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail("order-1", "mentee@example.com"))
                .thenReturn(Optional.of(payment));

        assertThat(paymentService.confirm("mentee@example.com", "order-1", "key-1", 50_000))
                .isSameAs(payment);
        org.mockito.Mockito.verifyNoInteractions(tossPaymentClient);
    }

    @Test
    void canceledMatchIsRejectedBeforeGatewayCall() {
        Match match = approvedMatch();
        Payment payment = Payment.ready(match, 50_000, PaymentProvider.TOSS, "order-1");
        match.cancel(LocalDateTime.of(2026, 11, 3, 12, 0));
        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail("order-1", "mentee@example.com"))
                .thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.confirm("mentee@example.com", "order-1", "key-1", 50_000))
                .isInstanceOf(IllegalStateException.class);
        org.mockito.Mockito.verifyNoInteractions(tossPaymentClient);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
    }

    @Test
    void amountMismatchIsRejectedBeforeGatewayCall() {
        Payment payment = Payment.ready(approvedMatch(), 50_000, PaymentProvider.TOSS, "order-1");
        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail("order-1", "mentee@example.com"))
                .thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.confirm("mentee@example.com", "order-1", "key-1", 1))
                .isInstanceOf(IllegalArgumentException.class);
        org.mockito.Mockito.verifyNoInteractions(tossPaymentClient);
    }

    @Test
    void readyUsesProductPriceAndStoresPendingPayment() {
        com.careerbridge.product.entity.Product product = org.mockito.Mockito.mock(
                com.careerbridge.product.entity.Product.class);
        when(product.getPrice()).thenReturn(50_000);
        Match match = new Match(1L, product, null, MatchStatus.APPROVED,
                null, null, null, null, null, null);
        when(matchRepository.findByIdAndMenteeUserEmail(1L, "mentee@example.com"))
                .thenReturn(Optional.of(match));
        when(paymentRepository.save(org.mockito.ArgumentMatchers.any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.ready("mentee@example.com", 1L);

        assertThat(payment.getAmount()).isEqualTo(50_000);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(payment.getOrderId()).startsWith("payment-");
        org.mockito.Mockito.verifyNoInteractions(tossPaymentClient);
    }

    @Test
    void readyRejectsDuplicatePayment() {
        when(matchRepository.findByIdAndMenteeUserEmail(1L, "mentee@example.com"))
                .thenReturn(Optional.of(approvedMatch()));
        when(paymentRepository.existsByMatchId(1L)).thenReturn(true);

        assertThatThrownBy(() -> paymentService.ready("mentee@example.com", 1L))
                .isInstanceOf(IllegalStateException.class);
        org.mockito.Mockito.verify(paymentRepository, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(Payment.class));
    }

    private Match approvedMatch() {
        return new Match(
                1L, null, null, MatchStatus.APPROVED,
                null, LocalDateTime.of(2026, 11, 3, 11, 0),
                null, null, null, null
        );
    }
}
