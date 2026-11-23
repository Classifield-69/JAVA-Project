package com.gamecollection.controller;

import com.gamecollection.dto.UserDto;
import com.gamecollection.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // @AuthenticationPrincipal дава потребителя, когото филтърът е записал от токена
    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal UserPrincipal principal) {
        return UserDto.from(principal.getUser());
    }
}