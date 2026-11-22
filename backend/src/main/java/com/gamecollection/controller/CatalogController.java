package com.gamecollection.controller;

import com.gamecollection.dto.NamedDto;
import com.gamecollection.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {

    private final GameService gameService;

    // GET /api/genres
    @GetMapping("/genres")
    public List<NamedDto> getGenres() {
        return gameService.getGenres();
    }

    // GET /api/platforms
    @GetMapping("/platforms")
    public List<NamedDto> getPlatforms() {
        return gameService.getPlatforms();
    }
}