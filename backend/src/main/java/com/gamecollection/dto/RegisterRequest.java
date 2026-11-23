package com.gamecollection.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        @Pattern(regexp = "^[A-Za-z0-9_]+$",
                 message = "Username can contain only letters, digits and _")
        String username,

        @NotBlank(message = "Email is required")
        @Size(max = 255, message = "Email is too long")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
               message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                 message = "Password must contain at least one letter and one digit")
        String password) {
}