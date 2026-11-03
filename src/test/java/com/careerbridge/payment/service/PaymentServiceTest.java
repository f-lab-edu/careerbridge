package com.careerbridge.payment.service;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.match.domain.Match;
import com.careerbridge.match.domain.MatchStatus;
import com.careerbridge.match.repository.MatchRepository;
import com.careerbridge.mentee.entity.Mentee;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.entity.VerificationStatus;
import com.careerbridge.mentor.entity.VisibilityStatus;
import com.careerbridge.payment.client.TossPaymentClient;
import com.careerbridge.payment.entity.Payment;
import com.careerbridge.payment.entity.PaymentApproval;
import com.careerbridge.payment.entity.PaymentMethod;
import com.careerbridge.payment.entity.PaymentProvider;
import com.careerbridge.payment.entity.PaymentStatus;
import com.careerbridge.payment.repository.PaymentRepository;
import com.careerbridge.product.entity.Product;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final String MENTEE_EMAIL = "mentee@example.com";
    private static final String ORDER_ID = "payment-order-id";
    private static final String PAYMENT_KEY = "payment-key";

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
                paymentRepository,
                matchRepository,
                tossPaymentClient
        );
    }

    @Test
    @DisplayName("ready creates a READY payment for an approved match")
    void readyCreatesPaymentForApprovedMatch() {
        Match match = createMatch(MatchStatus.APPROVED);

        when(matchRepository.findByIdAndMenteeUserEmail(1L, MENTEE_EMAIL))
                .thenReturn(Optional.of(match));
        when(paymentRepository.existsByMatchId(1L))
                .thenReturn(false);
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.ready(MENTEE_EMAIL, 1L);

        assertThat(payment.getMatch()).isSameAs(match);
        assertThat(payment.getAmount()).isEqualTo(50_000);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(payment.getPaymentProvider()).isEqualTo(PaymentProvider.TOSS);
        assertThat(payment.getOrderId()).startsWith("payment-");
        assertThat(payment.getPaymentMethod()).isNull();
        assertThat(payment.getPaymentKey()).isNull();
        assertThat(payment.getPaidAt()).isNull();

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue()).isSameAs(payment);
    }

    @Test
    @DisplayName("ready rejects a match that is not approved")
    void readyRejectsNonApprovedMatch() {
        Match match = createMatch(MatchStatus.REQUESTED);

        when(matchRepository.findByIdAndMenteeUserEmail(1L, MENTEE_EMAIL))
                .thenReturn(Optional.of(match));

        assertThatThrownBy(() -> paymentService.ready(MENTEE_EMAIL, 1L))
                .isInstanceOf(IllegalStateException.class);

        verify(paymentRepository, never()).existsByMatchId(1L);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("ready rejects duplicate payment creation for the same match")
    void readyRejectsDuplicatePayment() {
        Match match = createMatch(MatchStatus.APPROVED);

        when(matchRepository.findByIdAndMenteeUserEmail(1L, MENTEE_EMAIL))
                .thenReturn(Optional.of(match));
        when(paymentRepository.existsByMatchId(1L))
                .thenReturn(true);

        assertThatThrownBy(() -> paymentService.ready(MENTEE_EMAIL, 1L))
                .isInstanceOf(IllegalStateException.class);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("confirm completes payment and marks match as paid after PG approval")
    void confirmCompletesPaymentAndMatch() {
        Match match = createMatch(MatchStatus.APPROVED);
        Payment payment = Payment.ready(
                match,
                match.getProduct().getPrice(),
                PaymentProvider.TOSS,
                ORDER_ID
        );
        LocalDateTime approvedAt = LocalDateTime.of(2026, 8, 10, 12, 0);
        PaymentApproval approval = new PaymentApproval(
                PAYMENT_KEY,
                PaymentMethod.CARD,
                approvedAt
        );

        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail(ORDER_ID, MENTEE_EMAIL))
                .thenReturn(Optional.of(payment));
        when(tossPaymentClient.confirm(PAYMENT_KEY, ORDER_ID, 50_000))
                .thenReturn(approval);

        Payment confirmedPayment = paymentService.confirm(
                MENTEE_EMAIL,
                ORDER_ID,
                PAYMENT_KEY,
                50_000
        );

        assertThat(confirmedPayment).isSameAs(payment);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getPaymentKey()).isEqualTo(PAYMENT_KEY);
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(payment.getPaidAt()).isEqualTo(approvedAt);
        assertThat(match.getMatchStatus()).isEqualTo(MatchStatus.PAID);
        assertThat(match.getPaidAt()).isEqualTo(approvedAt);
    }

    @Test
    @DisplayName("confirm rejects amount mismatch before calling PG")
    void confirmRejectsAmountMismatch() {
        Match match = createMatch(MatchStatus.APPROVED);
        Payment payment = Payment.ready(
                match,
                match.getProduct().getPrice(),
                PaymentProvider.TOSS,
                ORDER_ID
        );

        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail(ORDER_ID, MENTEE_EMAIL))
                .thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.confirm(
                MENTEE_EMAIL,
                ORDER_ID,
                PAYMENT_KEY,
                30_000
        ))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(tossPaymentClient);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(match.getMatchStatus()).isEqualTo(MatchStatus.APPROVED);
    }

    @Test
    @DisplayName("confirm rejects payment that is not READY before calling PG")
    void confirmRejectsNonReadyPayment() {
        Match match = createMatch(MatchStatus.APPROVED);
        Payment payment = Payment.ready(
                match,
                match.getProduct().getPrice(),
                PaymentProvider.TOSS,
                ORDER_ID
        );
        payment.complete(PAYMENT_KEY, PaymentMethod.CARD, LocalDateTime.now());

        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail(ORDER_ID, MENTEE_EMAIL))
                .thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.confirm(
                MENTEE_EMAIL,
                ORDER_ID,
                PAYMENT_KEY,
                50_000
        ))
                .isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(tossPaymentClient);
    }

    @Test
    @DisplayName("findById returns only payment owned by the mentee")
    void findByIdReturnsOwnedPayment() {
        Match match = createMatch(MatchStatus.APPROVED);
        Payment payment = Payment.ready(
                match,
                match.getProduct().getPrice(),
                PaymentProvider.TOSS,
                ORDER_ID
        );

        when(paymentRepository.findByIdAndMatchMenteeUserEmail(10L, MENTEE_EMAIL))
                .thenReturn(Optional.of(payment));

        Payment result = paymentService.findById(MENTEE_EMAIL, 10L);

        assertThat(result).isSameAs(payment);
    }

    @Test
    @DisplayName("findById rejects missing or unowned payment")
    void findByIdRejectsMissingPayment() {
        when(paymentRepository.findByIdAndMatchMenteeUserEmail(10L, MENTEE_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.findById(MENTEE_EMAIL, 10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void readyRejectsMissingOrUnownedMatch() {
        when(matchRepository.findByIdAndMenteeUserEmail(1L, MENTEE_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.ready(MENTEE_EMAIL, 1L))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(paymentRepository, tossPaymentClient);
    }

    @Test
    void confirmRejectsMissingOrUnownedPayment() {
        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail(ORDER_ID, MENTEE_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.confirm(MENTEE_EMAIL, ORDER_ID, PAYMENT_KEY, 50_000))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(tossPaymentClient);
    }

    @Test
    void confirmKeepsStateWhenGatewayFails() {
        Match match = createMatch(MatchStatus.APPROVED);
        Payment payment = Payment.ready(match, 50_000, PaymentProvider.TOSS, ORDER_ID);
        IllegalStateException failure = new IllegalStateException("Gateway unavailable");
        when(paymentRepository.findByOrderIdAndMatchMenteeUserEmail(ORDER_ID, MENTEE_EMAIL))
                .thenReturn(Optional.of(payment));
        when(tossPaymentClient.confirm(PAYMENT_KEY, ORDER_ID, 50_000)).thenThrow(failure);

        assertThatThrownBy(() -> paymentService.confirm(MENTEE_EMAIL, ORDER_ID, PAYMENT_KEY, 50_000))
                .isSameAs(failure);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(payment.getPaymentKey()).isNull();
        assertThat(payment.getPaidAt()).isNull();
        assertThat(match.getMatchStatus()).isEqualTo(MatchStatus.APPROVED);
        assertThat(match.getPaidAt()).isNull();
    }

    private Match createMatch(MatchStatus matchStatus) {
        JobCategory backend = new JobCategory(1L, "Backend", null, List.of());
        User mentorUser = User.create(
                "mentor@example.com",
                "EncodedPassword1!",
                "Mentor",
                UserRole.MENTOR
        );
        User menteeUser = User.create(
                MENTEE_EMAIL,
                "EncodedPassword1!",
                "Mentee",
                UserRole.MENTEE
        );
        Mentor mentor = new Mentor(
                1L,
                mentorUser,
                "Naver",
                "Backend Developer",
                backend,
                7,
                "Spring Boot mentoring.",
                VerificationStatus.APPROVED,
                VisibilityStatus.PUBLIC
        );
        Mentee mentee = new Mentee(
                1L,
                menteeUser,
                List.of(backend)
        );
        Product product = Product.create(
                mentor,
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50_000,
                60
        );

        return new Match(
                1L,
                product,
                mentee,
                matchStatus,
                LocalDateTime.now().minusDays(1),
                matchStatus == MatchStatus.APPROVED ? LocalDateTime.now() : null,
                null,
                null,
                null,
                null
        );
    }
}
