package com.gamecollection.repository;

import com.gamecollection.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Средна оценка на една игра (null, ако няма ревюта)
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.game.id = :gameId")
    Double findAverageRating(@Param("gameId") Long gameId);

    // Брой ревюта; Spring сам прави заявката по името на метода
    long countByGameId(Long gameId);

    // Средните оценки на много игри с ЕДНА заявка (за списъка)
    // Всеки ред е [gameId, avg]
    @Query("""
            SELECT r.game.id, AVG(r.rating) FROM Review r
            WHERE r.game.id IN :gameIds
            GROUP BY r.game.id
            """)
    List<Object[]> findAverageRatings(@Param("gameIds") Collection<Long> gameIds);

    // Има ли вече ревю от този потребител за тази игра (→ 409)
    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    // Ревютата на една игра, с пагинация; user и game идват със същата заявка (без N+1)
    @EntityGraph(attributePaths = {"user", "game"})
    Page<Review> findByGameId(Long gameId, Pageable pageable);

    // Моите ревюта, най-новите първи
    @EntityGraph(attributePaths = {"user", "game"})
    List<Review> findByUserIdOrderByIdDesc(Long userId);
}