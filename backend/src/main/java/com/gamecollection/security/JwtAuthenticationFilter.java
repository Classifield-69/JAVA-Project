package com.gamecollection.security;

import com.gamecollection.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                Long userId = jwtService.extractUserId(token);
                userRepository.findById(userId)
                        .map(UserPrincipal::new)
                        .filter(UserPrincipal::isEnabled)
                        .ifPresent(principal -> {
                            var authentication = new UsernamePasswordAuthenticationToken(
                                    principal, null, principal.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            } catch (JwtException | IllegalArgumentException ex) {
                // Невалиден/изтекъл токен: оставяме заявката анонимна.
                // Ако адресът е защитен, SecurityConfig ще върне 401.
                log.debug("Invalid JWT: {}", ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}