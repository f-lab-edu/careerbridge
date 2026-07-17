package com.careerbridge.product.dto;

import com.careerbridge.product.entity.ProductStatus;

public record ProductSearchRequest(Long mentorId,
                                   String keyword) {
}
