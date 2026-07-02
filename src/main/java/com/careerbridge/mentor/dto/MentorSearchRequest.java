package com.careerbridge.mentor.dto;

public record MentorSearchRequest(
        Long jobCategoryId,
        String keyword) {
}
