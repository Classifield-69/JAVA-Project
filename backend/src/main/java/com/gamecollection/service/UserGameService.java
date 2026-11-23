package com.gamecollection.service;

import com.gamecollection.dto.AddToCollectionRequest;
import com.gamecollection.dto.UserGameDto;
import com.gamecollection.entity.Game;
import com.gamecollection.entity.Platform;
import com.gamecollection.entity.UserGame;
import com.gamecollection.entity.enums.UserGameType;
import com.gamecollection.exception.BadRequestException;
import com.gamecollection.exception.ConflictException;
import com.gamecollection.exception.NotFoundException;
import com.gamecollection.repository.GameRepository;
import com.gamecollection.repository.PlatformRepository;
import com.gamecollection.repository.UserGameRepository;
import com.gamecollection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGameService {

    private final UserGameRepository userGameRepository;
    private final GameRepository gameRepository;
    private final PlatformRepository platformRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserGameDto> getMyCollection(Long userId, UserGameType type) {
        List<UserGame> items = (type == null)
                ? userGameRepository.findByUserIdOrderByIdDesc(userId)
                : userGameRepository.findByUserIdAndTypeOrderByIdDesc(userId, type);
        return items.stream().map(UserGameDto::from).toList();
    }

    @Transactional
    public UserGameDto add(Long userId, AddToCollectionRequest req) {
        Game game = gameRepository.findById(req.gameId())
                .orElseThrow(() -> new NotFoundException("Game not found"));
        Platform platform = platformRepository.findById(req.platformId())
                .orElseThrow(() -> new NotFoundException("Platform not found"));

        boolean releasedOnPlatform = game.getPlatforms().stream()
                .anyMatch(p -> p.getId().equals(platform.getId()));
        if (!releasedOnPlatform) {
            throw new BadRequestException(
                    game.getTitle() + " is not available on " + platform.getName());
        }

        if (userGameRepository.existsByUserIdAndGameIdAndPlatformId(
                userId, game.getId(), platform.getId())) {
            throw new ConflictException("This game is already in your collection on this platform");
        }

        UserGame ug = new UserGame();
        ug.setUser(userRepository.getReferenceById(userId));
        ug.setGame(game);
        ug.setPlatform(platform);
        ug.setType(req.type());

        return UserGameDto.from(userGameRepository.save(ug));
    }

    @Transactional
    public UserGameDto changeType(Long userId, Long id, UserGameType type) {
        UserGame ug = findOwned(userId, id);
        ug.setType(type);
        return UserGameDto.from(ug);
    }

    @Transactional
    public void remove(Long userId, Long id) {
        userGameRepository.delete(findOwned(userId, id));
    }

    private UserGame findOwned(Long userId, Long id) {
        UserGame ug = userGameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Collection entry not found"));
        if (!ug.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only change your own collection");
        }
        return ug;
    }
}