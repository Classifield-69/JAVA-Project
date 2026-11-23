package com.gamecollection.security;

import com.gamecollection.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final UserRepository userRepository;

    @Value("${app.security.max-login-attempts}")
    private int maxAttempts;

    @Value("${app.security.lock-duration}")
    private Duration lockDuration;

    // REQUIRES_NEW = отделна транзакция, която се записва веднага.
    // Така броячът остава в базата, дори после да хвърлим грешка.
    // Връща true, ако с този опит акаунтът е заключен.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean loginFailed(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(user -> {
            int attempts = user.getFailedLoginAttempts() + 1;
            if (attempts >= maxAttempts) {
                user.setLockedUntil(LocalDateTime.now().plus(lockDuration));
                user.setFailedLoginAttempts(0);
                log.warn("User {} locked until {}", user.getId(), user.getLockedUntil());
                return true;
            }
            user.setFailedLoginAttempts(attempts);
            return false;
        }).orElse(false);   // несъществуващ имейл: няма какво да броим
        // save() не е нужен: в транзакция Hibernate сам записва промените (dirty checking)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loginSucceeded(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
        });
    }
}