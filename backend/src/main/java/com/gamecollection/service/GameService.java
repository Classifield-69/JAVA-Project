package com.gamecollection.service;

import com.gamecollection.dto.GameDetailsDto;
import com.gamecollection.dto.GameSummaryDto;
import com.gamecollection.dto.NamedDto;
import com.gamecollection.dto.PageResponse;
import com.gamecollection.entity.Game;
import com.gamecollection.entity.Genre;
import com.gamecollection.entity.Platform;
import com.gamecollection.exception.ResourceNotFoundException;
import com.gamecollection.repository.GameRepository;
import com.gamecollection.repository.GenreRepository;
import com.gamecollection.repository.PlatformRepository;
import com.gamecollection.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor               // Lombok: конструктор за всички final полета
@Transactional(readOnly = true)        // всички методи само четат
public class GameService {

    private static final int MAX_PAGE_SIZE = 50;

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;
    private final ReviewRepository reviewRepository;

    // ---------- Каталог ----------

    public PageResponse<GameSummaryDto> searchGames(String search, Long platformId, Long genreId,
                                                    int page, int size) {
        String normalized = (search == null) ? "" : search.trim().toLowerCase();
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("title"));
        Page<Game> result = gameRepository.search(normalized, platformId, genreId, pageable);

        Map<Long, Double> ratings = averageRatings(result.getContent());

        List<GameSummaryDto> content = result.getContent().stream()
                .map(game -> new GameSummaryDto(
                        game.getId(),
                        game.getTitle(),
                        game.getCoverUrl(),
                        game.getReleaseDate(),
                        platformsOf(game),
                        genresOf(game),
                        round(ratings.get(game.getId()))))
                .toList();

        return new PageResponse<>(content, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    public GameDetailsDto getGame(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game with id " + id + " not found"));

        return new GameDetailsDto(
                game.getId(),
                game.getTitle(),
                game.getDeveloper(),
                game.getReleaseDate(),
                game.getDescription(),
                game.getCoverUrl(),
                platformsOf(game),
                genresOf(game),
                round(reviewRepository.findAverageRating(id)),
                reviewRepository.countByGameId(id));
    }

    // ---------- Справочници за филтрите ----------

    public List<NamedDto> getGenres() {
        return genreRepository.findAll(Sort.by("name")).stream()
                .map(g -> new NamedDto(g.getId(), g.getName()))
                .toList();
    }

    public List<NamedDto> getPlatforms() {
        return platformRepository.findAll(Sort.by("id")).stream()
                .map(p -> new NamedDto(p.getId(), p.getName()))
                .toList();
    }

    // ---------- Помощни методи ----------

    private List<NamedDto> platformsOf(Game game) {
        return game.getPlatforms().stream()
                .sorted(Comparator.comparing(Platform::getId))
                .map(p -> new NamedDto(p.getId(), p.getName()))
                .toList();
    }

    private List<NamedDto> genresOf(Game game) {
        return game.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getName))
                .map(g -> new NamedDto(g.getId(), g.getName()))
                .toList();
    }

    // Средните оценки на всички игри от страницата с една заявка
    private Map<Long, Double> averageRatings(List<Game> games) {
        if (games.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = games.stream().map(Game::getId).toList();
        Map<Long, Double> map = new HashMap<>();
        for (Object[] row : reviewRepository.findAverageRatings(ids)) {
            map.put((Long) row[0], (Double) row[1]);
        }
        return map;
    }

    // 8.6666 → 8.7; null остава null (няма ревюта)
    private Double round(Double value) {
        return (value == null) ? null : Math.round(value * 10) / 10.0;
    }
}