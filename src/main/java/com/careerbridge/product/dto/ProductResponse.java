package com.careerbridge.product.dto;

import com.careerbridge.product.entity.Product;
import com.careerbridge.product.entity.ProductStatus;

public record ProductResponse(Long id,
                              Long mentorId,
                              String title,
                              String description,
                              Integer price,
                              Integer duration,
                              ProductStatus productStatus) {
    public static ProductResponse from(Product product){
        return new ProductResponse(product.getId(),
                                   product.getMentor().getId(),
                                    product.getTitle(),
                                    product.getDescription(),
                                    product.getPrice(),
                                    product.getDuration(),
                                    product.getProductStatus());
    }
}
