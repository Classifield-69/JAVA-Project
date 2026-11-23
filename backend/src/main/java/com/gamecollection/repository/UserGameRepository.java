package com.gamecollection.repository;

import com.gamecollection.entity.UserGame;
import com.gamecollection.entity.enums.UserGameType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGameRepository extends JpaRepository<UserGame, Long> {

    boolean existsByUserIdAndGameIdAndPlatformId(Long userId, Long gameId, Long platformId);

    @EntityGraph(attributePaths = {"game", "platform"})
    List<UserGame> findByUserIdOrderByIdDesc(Long userId);

    @EntityGraph(attributePaths = {"game", "platform"})
    List<UserGame> findByUserIdAndTypeOrderByIdDesc(Long userId, UserGameType type);
}