package com.careerbridge.product.service;

import com.careerbridge.global.security.AuthenticatedUser;
import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.entity.VerificationStatus;
import com.careerbridge.mentor.entity.VisibilityStatus;
import com.careerbridge.mentor.repository.MentorRepository;
import com.careerbridge.product.entity.Product;
import com.careerbridge.product.entity.ProductStatus;
import com.careerbridge.product.repository.ProductRepository;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private UserRepository userRepository;

    private ProductService productService;

    private User mentorUser;
    private User otherMentorUser;
    private Mentor mentor;
    private Mentor otherMentor;
    private AuthenticatedUser mentorAuth;
    private AuthenticatedUser otherMentorAuth;
    private AuthenticatedUser menteeAuth;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, mentorRepository, userRepository);

        JobCategory backend = new JobCategory(1L, "Backend", null, List.of());
        mentorUser = User.create("mentor@example.com", "EncodedPassword1!", "Mentor User", UserRole.MENTOR);
        otherMentorUser = User.create("other@example.com", "EncodedPassword1!", "Other Mentor", UserRole.MENTOR);

        mentor = new Mentor(
                1L,
                mentorUser,
                "Naver",
                "Backend Developer",
                backend,
                7,
                "Spring Boot backend mentoring.",
                VerificationStatus.APPROVED,
                VisibilityStatus.PUBLIC
        );
        otherMentor = new Mentor(
                2L,
                otherMentorUser,
                "Kakao",
                "Frontend Developer",
                backend,
                5,
                "React frontend mentoring.",
                VerificationStatus.APPROVED,
                VisibilityStatus.PUBLIC
        );

        mentorAuth = new AuthenticatedUser("mentor@example.com", UserRole.MENTOR);
        otherMentorAuth = new AuthenticatedUser("other@example.com", UserRole.MENTOR);
        menteeAuth = new AuthenticatedUser("mentee@example.com", UserRole.MENTEE);
    }

    @Test
    @DisplayName("멘토는 상품을 등록할 수 있다")
    void createProductByMentor() {
        when(userRepository.findByEmailAndStatus("mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(mentorUser));
        when(mentorRepository.findByUser(mentorUser))
                .thenReturn(Optional.of(mentor));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product product = productService.create(
                mentorAuth,
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50000,
                60
        );

        assertThat(product.getMentor()).isEqualTo(mentor);
        assertThat(product.getTitle()).isEqualTo("Backend Mentoring");
        assertThat(product.getDescription()).isEqualTo("Spring Boot mentoring.");
        assertThat(product.getPrice()).isEqualTo(50000);
        assertThat(product.getDuration()).isEqualTo(60);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.ACTIVE);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getMentor().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("멘티는 상품을 등록할 수 없다")
    void createProductRejectsMentee() {
        assertThatThrownBy(() -> productService.create(
                menteeAuth,
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50000,
                60
        ))
                .isInstanceOf(SecurityException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품을 등록한 멘토는 상품을 수정할 수 있다")
    void updateProductByOwnerMentor() {
        Product product = Product.create(mentor, "Old Title", "Old description.", 30000, 30);

        when(userRepository.findByEmailAndStatus("mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(mentorUser));
        when(mentorRepository.findByUser(mentorUser))
                .thenReturn(Optional.of(mentor));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.update(
                mentorAuth,
                1L,
                "New Title",
                "New description.",
                70000,
                90,
                ProductStatus.ACTIVE
        );

        assertThat(product.getTitle()).isEqualTo("New Title");
        assertThat(product.getDescription()).isEqualTo("New description.");
        assertThat(product.getPrice()).isEqualTo(70000);
        assertThat(product.getDuration()).isEqualTo(90);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("다른 멘토의 상품은 수정할 수 없다")
    void updateProductRejectsNonOwnerMentor() {
        Product product = Product.create(mentor, "Owner Product", "Owner description.", 50000, 60);

        when(userRepository.findByEmailAndStatus("other@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(otherMentorUser));
        when(mentorRepository.findByUser(otherMentorUser))
                .thenReturn(Optional.of(otherMentor));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.update(
                otherMentorAuth,
                1L,
                "Hacked Title",
                "Hacked description.",
                1000,
                10,
                ProductStatus.ACTIVE
        ))
                .isInstanceOf(SecurityException.class);

        assertThat(product.getTitle()).isEqualTo("Owner Product");
        assertThat(product.getPrice()).isEqualTo(50000);
    }

    @Test
    @DisplayName("상품을 등록한 멘토는 상품을 삭제할 수 있다")
    void deleteProductByOwnerMentor() {
        Product product = Product.create(mentor, "Backend Mentoring", "Spring Boot mentoring.", 50000, 60);

        when(userRepository.findByEmailAndStatus("mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(mentorUser));
        when(mentorRepository.findByUser(mentorUser))
                .thenReturn(Optional.of(mentor));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.delete(mentorAuth, 1L);

        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("다른 멘토의 상품은 삭제할 수 없다")
    void deleteProductRejectsNonOwnerMentor() {
        Product product = Product.create(mentor, "Backend Mentoring", "Spring Boot mentoring.", 50000, 60);

        when(userRepository.findByEmailAndStatus("other@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(otherMentorUser));
        when(mentorRepository.findByUser(otherMentorUser))
                .thenReturn(Optional.of(otherMentor));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.delete(otherMentorAuth, 1L))
                .isInstanceOf(SecurityException.class);

        verify(productRepository, never()).delete(any());
    }

    @Test
    @DisplayName("상품 목록은 검색어를 trim하고 활성 상품만 조회한다")
    void searchProductsUsesTrimmedKeywordAndActiveStatus() {
        Product product = Product.create(mentor, "Backend Mentoring", "Spring Boot mentoring.", 50000, 60);

        when(productRepository.searchProducts(1L, "backend", ProductStatus.ACTIVE))
                .thenReturn(List.of(product));

        List<Product> products = productService.searchProducts(1L, "  backend  ");

        assertThat(products).containsExactly(product);
        verify(productRepository).searchProducts(1L, "backend", ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("검색어가 공백이면 null로 변환해서 조회한다")
    void searchProductsConvertsBlankKeywordToNull() {
        when(productRepository.searchProducts(1L, null, ProductStatus.ACTIVE))
                .thenReturn(List.of());

        List<Product> products = productService.searchProducts(1L, "   ");

        assertThat(products).isEmpty();
        verify(productRepository).searchProducts(1L, null, ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 상품은 수정할 수 없다")
    void updateRejectsUnknownProduct() {
        when(userRepository.findByEmailAndStatus("mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(mentorUser));
        when(mentorRepository.findByUser(mentorUser))
                .thenReturn(Optional.of(mentor));
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(
                mentorAuth,
                999L,
                "New Title",
                "New description.",
                70000,
                90,
                ProductStatus.ACTIVE
        ))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
