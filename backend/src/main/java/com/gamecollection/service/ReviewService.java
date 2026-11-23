package com.gamecollection.service;

import com.gamecollection.dto.ReviewDto;
import com.gamecollection.dto.ReviewRequest;
import com.gamecollection.entity.Game;
import com.gamecollection.entity.Review;
import com.gamecollection.exception.ConflictException;
import com.gamecollection.exception.NotFoundException;
import com.gamecollection.repository.GameRepository;
import com.gamecollection.repository.ReviewRepository;
import com.gamecollection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ReviewDto> getForGame(Long gameId, Pageable pageable) {
        if (!gameRepository.existsById(gameId)) {
            throw new NotFoundException("Game not found");
        }
        return reviewRepository.findByGameId(gameId, pageable).map(ReviewDto::from);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getMine(Long userId) {
        return reviewRepository.findByUserIdOrderByIdDesc(userId)
                .stream().map(ReviewDto::from).toList();
    }

    @Transactional
    public ReviewDto create(Long userId, Long gameId, ReviewRequest req) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));

        if (reviewRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw new ConflictException("You have already reviewed this game");
        }

        Review review = new Review();
        review.setUser(userRepository.getReferenceById(userId));
        review.setGame(game);
        review.setRating(req.rating().shortValue());
        review.setText(req.text().trim());

        return ReviewDto.from(reviewRepository.saveAndFlush(review));
    }

    @Transactional
    public ReviewDto update(Long userId, Long reviewId, ReviewRequest req) {
        Review review = findReview(reviewId);
        if (!isOwner(review, userId)) {
            throw new AccessDeniedException("You can only edit your own reviews");
        }
        review.setRating(req.rating().shortValue());
        review.setText(req.text().trim());
        return ReviewDto.from(reviewRepository.saveAndFlush(review));
    }

    @Transactional
    public void delete(Long userId, boolean isAdmin, Long reviewId) {
        Review review = findReview(reviewId);
        if (!isOwner(review, userId) && !isAdmin) {
            throw new AccessDeniedException("You can only delete your own reviews");
        }
        reviewRepository.delete(review);
    }

    private Review findReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));
    }

    private boolean isOwner(Review review, Long userId) {
        return review.getUser().getId().equals(userId);
    }
}