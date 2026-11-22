package com.gamecollection.dto;

import java.util.List;

// Наш формат за страниране, вместо да връщаме Spring-овия Page директно
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}