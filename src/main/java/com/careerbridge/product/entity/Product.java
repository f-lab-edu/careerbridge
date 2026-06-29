package com.careerbridge.product.entity;

import com.careerbridge.global.entity.BaseTimeEntity;
import com.careerbridge.mentor.entity.Mentor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Entity
@Table(name = "products")
@Getter
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false, unique = true)
    private Mentor mentor;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Min(0)
    private Integer price;

    @Min(0)
    @Column(nullable = false)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    protected Product() {

    }

    public Product(Long id,
                   Mentor mentor,
                   String description,
                   Integer price,
                   Integer duration){
        this.id = id;
        this.mentor = mentor;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }

}
