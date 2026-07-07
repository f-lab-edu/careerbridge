package com.careerbridge.product.dto;

import com.careerbridge.product.entity.ProductStatus;

public record ProductUpdateRequest(Long productId,
                                   String title,
                                   String description,
                                   Integer price,
                                   Integer duration,
                                   ProductStatus productStatus) {
}
