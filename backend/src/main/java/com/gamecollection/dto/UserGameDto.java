package com.gamecollection.dto;

import com.gamecollection.entity.UserGame;
import com.gamecollection.entity.enums.UserGameType;

public record UserGameDto(
        Long id,
        Long gameId,
        String gameTitle,
        String coverUrl,
        Long platformId,
        String platformName,
        UserGameType type) {

    public static UserGameDto from(UserGame ug) {
        return new UserGameDto(
                ug.getId(),
                ug.getGame().getId(),
                ug.getGame().getTitle(),
                ug.getGame().getCoverUrl(),
                ug.getPlatform().getId(),
                ug.getPlatform().getName(),
                ug.getType());
    }
}