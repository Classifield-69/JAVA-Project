package com.gamecollection.controller;

import com.gamecollection.dto.ReviewDto;
import com.gamecollection.dto.ReviewRequest;
import com.gamecollection.security.UserPrincipal;
import com.gamecollection.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // публично
    @GetMapping("/api/games/{gameId}/reviews")
    public Page<ReviewDto> getForGame(@PathVariable Long gameId,
                                      @PageableDefault(size = 10, sort = "id",
                                              direction = Sort.Direction.DESC) Pageable pageable) {
        return reviewService.getForGame(gameId, pageable);
    }

    @GetMapping("/api/users/me/reviews")
    public List<ReviewDto> getMine(@AuthenticationPrincipal UserPrincipal me) {
        return reviewService.getMine(me.getId());
    }

    @PostMapping("/api/games/{gameId}/reviews")
    public ResponseEntity<ReviewDto> create(@AuthenticationPrincipal UserPrincipal me,
                                            @PathVariable Long gameId,
                                            @Valid @RequestBody ReviewRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(me.getId(), gameId, req));
    }

    @PutMapping("/api/reviews/{id}")
    public ReviewDto update(@AuthenticationPrincipal UserPrincipal me,
                            @PathVariable Long id,
                            @Valid @RequestBody ReviewRequest req) {
        return reviewService.update(me.getId(), id, req);
    }

    @DeleteMapping("/api/reviews/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal me,
                                       @PathVariable Long id) {
        boolean isAdmin = me.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        reviewService.delete(me.getId(), isAdmin, id);
        return ResponseEntity.noContent().build();
    }
}