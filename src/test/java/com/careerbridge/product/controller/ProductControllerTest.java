package com.careerbridge.product.controller;

import com.careerbridge.global.security.AuthenticatedUser;
import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.entity.VerificationStatus;
import com.careerbridge.mentor.entity.VisibilityStatus;
import com.careerbridge.product.dto.ProductCreateRequest;
import com.careerbridge.product.dto.ProductResponse;
import com.careerbridge.product.dto.ProductUpdateRequest;
import com.careerbridge.product.entity.Product;
import com.careerbridge.product.entity.ProductStatus;
import com.careerbridge.product.service.ProductService;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController productController;
    private AuthenticatedUser authenticatedUser;
    private Mentor mentor;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productService);
        authenticatedUser = new AuthenticatedUser("mentor@example.com", UserRole.MENTOR);

        User user = User.create("mentor@example.com", "EncodedPassword1!", "Mentor User", UserRole.MENTOR);
        JobCategory backend = new JobCategory(1L, "Backend", null, List.of());
        mentor = new Mentor(
                1L,
                user,
                "Naver",
                "Backend Developer",
                backend,
                7,
                "Spring Boot backend mentoring.",
                VerificationStatus.APPROVED,
                VisibilityStatus.PUBLIC
        );
    }

    @Test
    @DisplayName("상품 목록 조회 결과를 응답 DTO로 변환한다")
    void searchProducts() {
        Product product = Product.create(
                mentor,
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50000,
                60
        );
        when(productService.searchProducts(1L, "backend"))
                .thenReturn(List.of(product));

        ResponseEntity<List<ProductResponse>> response = productController.searchProduct(1L, "backend");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        ProductResponse productResponse = response.getBody().get(0);
        assertThat(productResponse.mentorId()).isEqualTo(1L);
        assertThat(productResponse.title()).isEqualTo("Backend Mentoring");
        assertThat(productResponse.description()).isEqualTo("Spring Boot mentoring.");
        assertThat(productResponse.price()).isEqualTo(50000);
        assertThat(productResponse.duration()).isEqualTo(60);
        assertThat(productResponse.productStatus()).isEqualTo(ProductStatus.ACTIVE);

        verify(productService).searchProducts(1L, "backend");
    }

    @Test
    @DisplayName("상품 등록 요청 DTO를 서비스 인자로 풀어서 전달한다")
    void createProduct() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50000,
                60
        );
        Product savedProduct = Product.create(
                mentor,
                request.title(),
                request.description(),
                request.price(),
                request.duration()
        );
        when(productService.create(
                authenticatedUser,
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50000,
                60
        ))
                .thenReturn(savedProduct);

        ResponseEntity<ProductResponse> response = productController.createProduct(authenticatedUser, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().mentorId()).isEqualTo(1L);
        assertThat(response.getBody().title()).isEqualTo("Backend Mentoring");
        assertThat(response.getBody().price()).isEqualTo(50000);
        assertThat(response.getBody().productStatus()).isEqualTo(ProductStatus.ACTIVE);

        verify(productService).create(
                authenticatedUser,
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50000,
                60
        );
    }

    @Test
    @DisplayName("상품 수정은 URL의 상품 id를 서비스에 전달한다")
    void updateProduct() {
        ProductUpdateRequest request = new ProductUpdateRequest(
                999L,
                "Updated Mentoring",
                "Updated description.",
                70000,
                90,
                ProductStatus.ACTIVE
        );

        ResponseEntity<Void> response = productController.updateProduct(authenticatedUser, 1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productService).update(
                authenticatedUser,
                1L,
                "Updated Mentoring",
                "Updated description.",
                70000,
                90,
                ProductStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("상품 삭제는 URL의 상품 id를 서비스에 전달한다")
    void deleteProduct() {
        ResponseEntity<Void> response = productController.deleteProduct(authenticatedUser, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productService).delete(authenticatedUser, 1L);
    }
}
