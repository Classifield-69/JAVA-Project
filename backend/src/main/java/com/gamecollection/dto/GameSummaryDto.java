package com.gamecollection.dto;

import java.time.LocalDate;
import java.util.List;

// Една карта в списъка /games
public record GameSummaryDto(
        Long id,
        String title,
        String coverUrl,
        LocalDate releaseDate,
        List<NamedDto> platforms,
        List<NamedDto> genres,
        Double averageRating
) {
}