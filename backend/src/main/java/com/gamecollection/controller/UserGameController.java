package com.gamecollection.controller;

import com.gamecollection.dto.AddToCollectionRequest;
import com.gamecollection.dto.UpdateCollectionRequest;
import com.gamecollection.dto.UserGameDto;
import com.gamecollection.entity.enums.UserGameType;
import com.gamecollection.security.UserPrincipal;
import com.gamecollection.service.UserGameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/games")
@RequiredArgsConstructor
public class UserGameController {

    private final UserGameService userGameService;

    @GetMapping
    public List<UserGameDto> getMyCollection(@AuthenticationPrincipal UserPrincipal me,
                                             @RequestParam(required = false) UserGameType type) {
        return userGameService.getMyCollection(me.getId(), type);
    }

    @PostMapping
    public ResponseEntity<UserGameDto> add(@AuthenticationPrincipal UserPrincipal me,
                                           @Valid @RequestBody AddToCollectionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userGameService.add(me.getId(), req));
    }

    @PatchMapping("/{id}")
    public UserGameDto changeType(@AuthenticationPrincipal UserPrincipal me,
                                  @PathVariable Long id,
                                  @Valid @RequestBody UpdateCollectionRequest req) {
        return userGameService.changeType(me.getId(), id, req.type());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal UserPrincipal me,
                                       @PathVariable Long id) {
        userGameService.remove(me.getId(), id);
        return ResponseEntity.noContent().build();
    }
}