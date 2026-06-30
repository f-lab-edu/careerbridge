package com.careerbridge.product.dto;

public record ProductCreateRequest(String title,
                                   String description,
                                   Integer price,
                                   Integer duration) {
}
