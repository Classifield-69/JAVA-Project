package com.gamecollection.dto;

import com.gamecollection.entity.enums.UserGameType;
import jakarta.validation.constraints.NotNull;

public record UpdateCollectionRequest(@NotNull UserGameType type) {
}