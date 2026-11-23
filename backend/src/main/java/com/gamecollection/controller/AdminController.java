package com.gamecollection.controller;

import com.gamecollection.dto.UserDto;
import com.gamecollection.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }
}