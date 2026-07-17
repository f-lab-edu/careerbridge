package com.careerbridge.product.entity;

import com.careerbridge.global.entity.BaseTimeEntity;
import com.careerbridge.mentor.entity.Mentor;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "products")
@Getter
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus productStatus;

    protected Product() {
    }

    private Product(
            Mentor mentor,
            String title,
            String description,
            Integer price,
            Integer duration
    ) {
        validate(title, description, price, duration);

        this.mentor = mentor;
        this.title = title;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.productStatus = ProductStatus.ACTIVE;
    }

    public static Product create(
            Mentor mentor,
            String title,
            String description,
            Integer price,
            Integer duration
    ) {
        if (mentor == null) {
            throw new IllegalArgumentException("멘토 정보가 필요합니다.");
        }

        return new Product(mentor, title, description, price, duration);
    }

    public void update(
            String title,
            String description,
            Integer price,
            Integer duration,
            ProductStatus productStatus
    ) {
        validate(title, description, price, duration);

        if (productStatus == null) {
            throw new IllegalArgumentException("상품 상태가 필요합니다.");
        }

        this.title = title;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.productStatus = productStatus;
    }

    public void makeDeactivate() {

        this.productStatus = ProductStatus.INACTIVE;
    }

    public boolean isOwnedBy(Mentor mentor) {

        return mentor != null && this.mentor.getId().equals(mentor.getId());
    }

    private void validate(String title, String description, Integer price, Integer duration) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("상품명이 필요합니다.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("상품 설명이 필요합니다.");
        }

        if (price == null || price < 0) {
            throw new IllegalArgumentException("상품 가격은 0원 이상이어야 합니다.");
        }

        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("멘토링 시간은 1분 이상이어야 합니다.");
        }
    }
}