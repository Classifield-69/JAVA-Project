package com.gamecollection.dto;

import com.gamecollection.entity.enums.UserGameType;
import jakarta.validation.constraints.NotNull;

public record AddToCollectionRequest(
        @NotNull Long gameId,
        @NotNull Long platformId,
        @NotNull UserGameType type) {
}