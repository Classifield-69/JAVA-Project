package com.gamecollection.repository;

import com.gamecollection.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Long> {

    // Търсене по име + незадължителни филтри по платформа и жанр.
    // Ако даден филтър е null, условието му винаги е вярно и не филтрира нищо.
    @Query("""
            SELECT DISTINCT g FROM Game g
            LEFT JOIN g.platforms p
            LEFT JOIN g.genres ge
            WHERE LOWER(g.title) LIKE CONCAT('%', :search, '%')
              AND (:platformId IS NULL OR p.id = :platformId)
              AND (:genreId IS NULL OR ge.id = :genreId)
            """)
    Page<Game> search(@Param("search") String search,
                      @Param("platformId") Long platformId,
                      @Param("genreId") Long genreId,
                      Pageable pageable);
}