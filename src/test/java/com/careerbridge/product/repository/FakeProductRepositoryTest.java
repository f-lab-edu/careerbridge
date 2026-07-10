package com.careerbridge.product.repository;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.entity.VerificationStatus;
import com.careerbridge.mentor.entity.VisibilityStatus;
import com.careerbridge.product.entity.Product;
import com.careerbridge.product.entity.ProductStatus;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FakeProductRepositoryTest {

    private FakeProductRepository productRepository;
    private Mentor backendMentor;
    private Mentor frontendMentor;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();

        JobCategory backend = new JobCategory(1L, "Backend", null, List.of());
        JobCategory frontend = new JobCategory(2L, "Frontend", null, List.of());

        backendMentor = new Mentor(
                1L,
                User.create("backend@example.com", "EncodedPassword1!", "Backend Mentor", UserRole.MENTOR),
                "Naver",
                "Backend Developer",
                backend,
                7,
                "Spring Boot backend experience.",
                VerificationStatus.APPROVED,
                VisibilityStatus.PUBLIC
        );

        frontendMentor = new Mentor(
                2L,
                User.create("frontend@example.com", "EncodedPassword1!", "Frontend Mentor", UserRole.MENTOR),
                "Kakao",
                "Frontend Developer",
                frontend,
                5,
                "React frontend experience.",
                VerificationStatus.APPROVED,
                VisibilityStatus.PUBLIC
        );
    }

    @Test
    @DisplayName("상품 등록 + 상품 조회")
    void saveAndFindById() {
        Product product = Product.create(
                backendMentor,
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50000,
                60
        );

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(productRepository.findById(savedProduct.getId())).contains(savedProduct);
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteProduct() {
        Product product = productRepository.save(Product.create(
                backendMentor,
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50000,
                60
        ));

        productRepository.delete(product);

        assertThat(productRepository.findById(product.getId())).isEmpty();
    }

    @Test
    @DisplayName("키워드 상품 검색")
    void searchProducts() {
        Product backendProduct = productRepository.save(Product.create(
                backendMentor,
                "Backend Mentoring",
                "Spring Boot mentoring.",
                50000,
                60
        ));

        Product frontendProduct = productRepository.save(Product.create(
                frontendMentor,
                "Frontend Mentoring",
                "React mentoring.",
                40000,
                45
        ));

        frontendProduct.update(
                frontendProduct.getTitle(),
                frontendProduct.getDescription(),
                frontendProduct.getPrice(),
                frontendProduct.getDuration(),
                ProductStatus.INACTIVE
        );

        List<Product> products = productRepository.searchProducts(
                backendMentor.getId(),
                "backend",
                ProductStatus.ACTIVE
        );

        assertThat(products).containsExactly(backendProduct);
    }

    @Test
    @DisplayName("멘토이름으로 상품 검색")
    void searchProductsByMentorName() {
        Product product = productRepository.save(Product.create(
                backendMentor,
                "Career Coaching",
                "Backend career coaching.",
                50000,
                60
        ));

        List<Product> products = productRepository.searchProducts(
                null,
                "backend mentor",
                ProductStatus.ACTIVE
        );

        assertThat(products).containsExactly(product);
    }
}
