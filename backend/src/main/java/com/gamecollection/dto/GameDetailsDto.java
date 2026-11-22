package com.gamecollection.dto;

import java.time.LocalDate;
import java.util.List;

// Страницата /games/:id
public record GameDetailsDto(
        Long id,
        String title,
        String developer,
        LocalDate releaseDate,
        String description,
        String coverUrl,
        List<NamedDto> platforms,
        List<NamedDto> genres,
        Double averageRating,
        long reviewCount
) {
}