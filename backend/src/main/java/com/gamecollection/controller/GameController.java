package com.gamecollection.controller;

import com.gamecollection.dto.GameDetailsDto;
import com.gamecollection.dto.GameSummaryDto;
import com.gamecollection.dto.PageResponse;
import com.gamecollection.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController                      // методите връщат JSON
@RequestMapping("/api/games")        // общото начало на адресите
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // GET /api/games?search=&platformId=&genreId=&page=0&size=12
    @GetMapping
    public PageResponse<GameSummaryDto> getGames(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long platformId,
            @RequestParam(required = false) Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return gameService.searchGames(search, platformId, genreId, page, size);
    }

    // GET /api/games/5
    @GetMapping("/{id}")
    public GameDetailsDto getGame(@PathVariable Long id) {
        return gameService.getGame(id);
    }
}