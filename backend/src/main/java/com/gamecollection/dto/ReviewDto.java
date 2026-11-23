package com.gamecollection.dto;

import com.gamecollection.entity.Review;

import java.time.LocalDateTime;

public record ReviewDto(
        Long id,
        Long gameId,
        String gameTitle,
        Long userId,
        String username,
        Integer rating,
        String text,
        LocalDateTime createdAt) {

    public static ReviewDto from(Review r) {
        return new ReviewDto(
                r.getId(),
                r.getGame().getId(),
                r.getGame().getTitle(),
                r.getUser().getId(),
                r.getUser().getUsername(),
                r.getRating().intValue(),
                r.getText(),
                r.getCreatedAt());
    }
}