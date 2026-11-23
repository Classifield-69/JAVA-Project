package com.gamecollection.dto;

import com.gamecollection.entity.User;
import com.gamecollection.entity.enums.AuthProvider;
import com.gamecollection.entity.enums.Role;

public record UserDto(Long id, String username, String email, Role role, AuthProvider authProvider) {

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(),
                user.getRole(), user.getAuthProvider());
    }
}