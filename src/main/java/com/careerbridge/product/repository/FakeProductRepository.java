package com.careerbridge.product.repository;

import com.careerbridge.product.entity.Product;
import com.careerbridge.product.entity.ProductStatus;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FakeProductRepository implements ProductRepository {

    private Long nextId = 1L;
    private final List<Product> products = new ArrayList<>();

    @Override
    public List<Product> searchProducts(Long mentorId, String keyword, ProductStatus status) {
        String normalizedKeyword = normalizeKeyword(keyword);

        return products.stream()
                .filter(product -> matchesStatus(product, status))
                .filter(product -> matchesMentorId(product, mentorId))
                .filter(product -> matchesKeyword(product, normalizedKeyword))
                .toList();
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            setId(product, nextId++);
            products.add(product);
            return product;
        }

        products.removeIf(savedProduct -> savedProduct.getId().equals(product.getId()));
        products.add(product);

        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    @Override
    public void delete(Product product) {
        if (product == null || product.getId() == null) {
            return;
        }

        products.removeIf(savedProduct -> savedProduct.getId().equals(product.getId()));
    }

    private boolean matchesStatus(Product product, ProductStatus status) {
        return status == null || product.getProductStatus() == status;
    }

    private boolean matchesMentorId(Product product, Long mentorId) {
        return mentorId == null || product.getMentor().getId().equals(mentorId);
    }

    private boolean matchesKeyword(Product product, String keyword) {
        if (keyword == null) {
            return true;
        }

        return product.getTitle().toLowerCase().contains(keyword)
                || product.getMentor().getMentorName().toLowerCase().contains(keyword);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim().toLowerCase();
    }

    private void setId(Product product, Long id) {
        try {
            Field idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(product, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Product id를 설정할 수 없습니다.", e);
        }
    }
}
