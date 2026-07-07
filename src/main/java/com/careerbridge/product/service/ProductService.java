package com.careerbridge.product.service;

import com.careerbridge.global.security.AuthenticatedUser;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.repository.MentorRepository;
import com.careerbridge.product.entity.Product;
import com.careerbridge.product.entity.ProductStatus;
import com.careerbridge.product.repository.ProductRepository;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;

    public List<Product> searchProducts(Long mentorId, String keyword) {

        return productRepository.searchProducts(
                        mentorId,
                        normalizeKeyword(keyword),
                        ProductStatus.ACTIVE
                ).stream().collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public Product create(
            AuthenticatedUser authenticatedUser,
            String title,
            String description,
            Integer price,
            Integer duration
    ) {

        Mentor mentor = getAuthenticatedMentor(authenticatedUser);

        Product product = Product.create(
                mentor,
                title,
                description,
                price,
                duration
        );

        return productRepository.save(product);
    }

    @Transactional
    public void update(
            AuthenticatedUser authenticatedUser,
            Long productId,
            String title,
            String description,
            Integer price,
            Integer duration,
            ProductStatus productStatus
    ) {
        Mentor mentor = getAuthenticatedMentor(authenticatedUser);
        Product product = getProduct(productId);

        validateProductOwner(product, mentor);

        product.update(
                title,
                description,
                price,
                duration,
                productStatus
        );
    }

    @Transactional
    public void delete(
            AuthenticatedUser authenticatedUser,
            Long productId
    ) {
        Mentor mentor = getAuthenticatedMentor(authenticatedUser);
        Product product = getProduct(productId);

        validateProductOwner(product, mentor);

        productRepository.delete(product);
    }

    private Mentor getAuthenticatedMentor(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser.role() != UserRole.MENTOR) {
            throw new SecurityException("멘토만 상품을 등록, 수정, 삭제할 수 있습니다.");
        }

        User user = userRepository.findByEmailAndStatus(
                        authenticatedUser.email(),
                        UserStatus.ACTIVE
                )
                .orElseThrow(() -> new IllegalArgumentException("활성화된 사용자 계정을 찾을 수 없습니다."));

        return mentorRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("멘토 정보를 찾을 수 없습니다."));
    }

    private Product getProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 id가 필요합니다.");
        }
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
    }

    private void validateProductOwner(Product product, Mentor mentor) {
        if (!product.isOwnedBy(mentor)) {
            throw new SecurityException("본인이 등록한 상품만 수정하거나 삭제할 수 있습니다.");
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }
}
