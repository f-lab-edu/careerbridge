package com.careerbridge.product.service;

import com.careerbridge.global.security.AuthenticatedUser;
import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.entity.VerificationStatus;
import com.careerbridge.mentor.entity.VisibilityStatus;
import com.careerbridge.mentor.repository.MentorRepository;
import com.careerbridge.product.dto.ProductCreateRequest;
import com.careerbridge.product.dto.ProductResponse;
import com.careerbridge.product.dto.ProductSearchRequest;
import com.careerbridge.product.dto.ProductUpdateRequest;
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
import static org.mockito.Mockito.*;

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
    private User menteeUser;
    private Mentor mentor;
    private Mentor otherMentor;
    private AuthenticatedUser mentorAuth;
    private AuthenticatedUser otherMentorAuth;
    private AuthenticatedUser menteeAuth;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                productRepository,
                mentorRepository,
                userRepository
        );

        JobCategory backend = new JobCategory(1L, "Backend", null, List.of());

        mentorUser = User.create(
                "mentor@example.com",
                "EncodedPassword1!",
                "김멘토",
                UserRole.MENTOR
        );

        otherMentorUser = User.create(
                "other-mentor@example.com",
                "EncodedPassword1!",
                "이멘토",
                UserRole.MENTOR
        );

        menteeUser = User.create(
                "mentee@example.com",
                "EncodedPassword1!",
                "박멘티",
                UserRole.MENTEE
        );

        mentor = new Mentor(
                1L,
                mentorUser,
                "Naver",
                "Backend Developer",
                backend,
                7,
                "Spring Boot 백엔드 개발 경험이 있습니다.",
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
                "React 프론트엔드 개발 경험이 있습니다.",
                VerificationStatus.APPROVED,
                VisibilityStatus.PUBLIC
        );

        mentorAuth = new AuthenticatedUser("mentor@example.com", UserRole.MENTOR);
        otherMentorAuth = new AuthenticatedUser("other-mentor@example.com", UserRole.MENTOR);
        menteeAuth = new AuthenticatedUser("mentee@example.com", UserRole.MENTEE);
    }

    @Test
    @DisplayName("멘토는 상품을 등록할 수 있다")
    void createProductByMentor() {
        ProductCreateRequest request = new ProductCreateRequest(
                "백엔드 멘토링",
                "Spring Boot 기반 백엔드 멘토링입니다.",
                50000,
                60
        );

        when(userRepository.findByEmailAndStatus("mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(mentorUser));
        when(mentorRepository.findByUser(mentorUser))
                .thenReturn(Optional.of(mentor));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.create(mentorAuth, request);

        assertThat(response.mentorId()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("백엔드 멘토링");
        assertThat(response.description()).isEqualTo("Spring Boot 기반 백엔드 멘토링입니다.");
        assertThat(response.price()).isEqualTo(50000);
        assertThat(response.duration()).isEqualTo(60);
        assertThat(response.productStatus()).isEqualTo(ProductStatus.ACTIVE);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getMentor().getId()).isEqualTo(1L);
        assertThat(savedProduct.getTitle()).isEqualTo("백엔드 멘토링");
        assertThat(savedProduct.getProductStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("멘티는 상품을 등록할 수 없다")
    void createProductRejectsMentee() {
        ProductCreateRequest request = new ProductCreateRequest(
                "백엔드 멘토링",
                "Spring Boot 기반 백엔드 멘토링입니다.",
                50000,
                60
        );

        assertThatThrownBy(() -> productService.create(menteeAuth, request))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("멘토만");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품을 등록한 멘토는 상품을 수정할 수 있다")
    void updateProductByOwnerMentor() {
        Product product = Product.create(
                mentor,
                "기존 상품명",
                "기존 설명",
                30000,
                30
        );

        ProductUpdateRequest request = new ProductUpdateRequest(
                "수정된 상품명",
                "수정된 설명",
                70000,
                90,
                ProductStatus.ACTIVE
        );

        when(userRepository.findByEmailAndStatus("mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(mentorUser));
        when(mentorRepository.findByUser(mentorUser))
                .thenReturn(Optional.of(mentor));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.update(mentorAuth, 1L, request);

        assertThat(response.title()).isEqualTo("수정된 상품명");
        assertThat(response.description()).isEqualTo("수정된 설명");
        assertThat(response.price()).isEqualTo(70000);
        assertThat(response.duration()).isEqualTo(90);
        assertThat(response.productStatus()).isEqualTo(ProductStatus.ACTIVE);

        assertThat(product.getTitle()).isEqualTo("수정된 상품명");
        assertThat(product.getDescription()).isEqualTo("수정된 설명");
        assertThat(product.getPrice()).isEqualTo(70000);
        assertThat(product.getDuration()).isEqualTo(90);
    }

    @Test
    @DisplayName("다른 멘토의 상품은 수정할 수 없다")
    void updateProductRejectsNonOwnerMentor() {
        Product product = Product.create(
                mentor,
                "백엔드 멘토링",
                "Spring Boot 기반 백엔드 멘토링입니다.",
                50000,
                60
        );

        ProductUpdateRequest request = new ProductUpdateRequest(
                "탈취 수정",
                "다른 멘토가 수정하려는 설명",
                1000,
                10,
                ProductStatus.ACTIVE
        );

        when(userRepository.findByEmailAndStatus("other-mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(otherMentorUser));
        when(mentorRepository.findByUser(otherMentorUser))
                .thenReturn(Optional.of(otherMentor));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.update(otherMentorAuth, 1L, request))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("본인이 등록한 상품");

        assertThat(product.getTitle()).isEqualTo("백엔드 멘토링");
        assertThat(product.getPrice()).isEqualTo(50000);
    }

    @Test
    @DisplayName("상품을 등록한 멘토는 상품을 삭제하면 비활성화된다")
    void deleteProductByOwnerMentor() {
        Product product = Product.create(
                mentor,
                "백엔드 멘토링",
                "Spring Boot 기반 백엔드 멘토링입니다.",
                50000,
                60
        );

        when(userRepository.findByEmailAndStatus("mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(mentorUser));
        when(mentorRepository.findByUser(mentorUser))
                .thenReturn(Optional.of(mentor));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.delete(mentorAuth, 1L);

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.INACTIVE);
        verify(productRepository, never()).delete(product);
    }

    @Test
    @DisplayName("다른 멘토의 상품은 삭제할 수 없다")
    void deleteProductRejectsNonOwnerMentor() {
        Product product = Product.create(
                mentor,
                "백엔드 멘토링",
                "Spring Boot 기반 백엔드 멘토링입니다.",
                50000,
                60
        );

        when(userRepository.findByEmailAndStatus("other-mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(otherMentorUser));
        when(mentorRepository.findByUser(otherMentorUser))
                .thenReturn(Optional.of(otherMentor));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.delete(otherMentorAuth, 1L))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("본인이 등록한 상품");

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("멘토명 또는 상품명 키워드로 활성 상품 목록을 조회한다")
    void searchProductsByKeyword() {
        Product product = Product.create(
                mentor,
                "백엔드 멘토링",
                "Spring Boot 기반 백엔드 멘토링입니다.",
                50000,
                60
        );

        ProductSearchRequest request = new ProductSearchRequest(
                1L,
                "백엔드"
        );

        when(productRepository.searchProducts(1L, "백엔드", ProductStatus.ACTIVE))
                .thenReturn(List.of(product));

        List<ProductResponse> responses = productService.searchProducts(request);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).mentorId()).isEqualTo(1L);
        assertThat(responses.get(0).title()).isEqualTo("백엔드 멘토링");

        verify(productRepository).searchProducts(1L, "백엔드", ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("검색어가 공백이면 null로 변환해서 조회한다")
    void searchProductsWithBlankKeyword() {
        ProductSearchRequest request = new ProductSearchRequest(
                1L,
                "   "
        );

        when(productRepository.searchProducts(1L, null, ProductStatus.ACTIVE))
                .thenReturn(List.of());

        List<ProductResponse> responses = productService.searchProducts(request);

        assertThat(responses).isEmpty();
        verify(productRepository).searchProducts(1L, null, ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 상품은 수정할 수 없다")
    void updateRejectsUnknownProduct() {
        ProductUpdateRequest request = new ProductUpdateRequest(
                "수정된 상품명",
                "수정된 설명",
                70000,
                90,
                ProductStatus.ACTIVE
        );

        when(userRepository.findByEmailAndStatus("mentor@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(mentorUser));
        when(mentorRepository.findByUser(mentorUser))
                .thenReturn(Optional.of(mentor));
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(mentorAuth, 999L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 상품");
    }
}