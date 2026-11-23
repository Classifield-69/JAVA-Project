package com.gamecollection.service;

import com.gamecollection.dto.AuthResponse;
import com.gamecollection.dto.LoginRequest;
import com.gamecollection.dto.RegisterRequest;
import com.gamecollection.dto.UserDto;
import com.gamecollection.entity.User;
import com.gamecollection.exception.ConflictException;
import com.gamecollection.repository.UserRepository;
import com.gamecollection.security.JwtService;
import com.gamecollection.security.LoginAttemptService;
import com.gamecollection.security.UserPrincipal;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        String username = request.username().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email is already registered");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("Username is already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        return toAuthResponse(userRepository.save(user));
    }

    // Без @Transactional нарочно: иначе грешката би върнала (rollback) и брояча
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password()));

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            loginAttemptService.loginSucceeded(principal.getUser().getId());
            return toAuthResponse(principal.getUser());

        } catch (BadCredentialsException ex) {
            boolean lockedNow = loginAttemptService.loginFailed(email);
            if (lockedNow) {
                throw new LockedException("Account locked");
            }
            throw ex;
        }
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user), "Bearer",
                jwtService.getExpirationSeconds(), UserDto.from(user));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}