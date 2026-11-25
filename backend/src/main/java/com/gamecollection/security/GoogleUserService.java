package com.gamecollection.security;

import com.gamecollection.entity.User;
import com.gamecollection.entity.enums.AuthProvider;
import com.gamecollection.repository.UserRepository;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleUserService {

    private final UserRepository userRepository;

    @Transactional
    public User findOrCreate(OidcUser googleUser) {
        String googleId = googleUser.getSubject();
        String email = googleUser.getEmail();

        if (email == null || !Boolean.TRUE.equals(googleUser.getEmailVerified())) {
            throw error("email_not_verified");
        }
        email = email.toLowerCase(Locale.ROOT);

        Optional<User> byGoogleId = userRepository.findByProviderId(googleId);
        User user;

        if (byGoogleId.isPresent()) {
            user = byGoogleId.get();
        } else {
            Optional<User> byEmail = userRepository.findByEmailIgnoreCase(email);
            if (byEmail.isPresent()) {
                user = byEmail.get();
                user.setProviderId(googleId);
                if (user.getAvatarUrl() == null) {
                    user.setAvatarUrl(googleUser.getPicture());
                }
            } else {
                user = new User();
                user.setEmail(email);
                user.setUsername(generateUsername(email));
                user.setAuthProvider(AuthProvider.GOOGLE);
                user.setProviderId(googleId);
                user.setAvatarUrl(googleUser.getPicture());
                user = userRepository.save(user);
            }
        }

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw error("account_disabled");
        }
        return user;
    }

    private String generateUsername(String email) {
        String base = email.substring(0, email.indexOf('@')).replaceAll("[^A-Za-z0-9_]", "_");
        if (base.length() < 3) {
            base = base + "_user";
        }
        if (base.length() > 25) {
            base = base.substring(0, 25);
        }
        String candidate = base;
        int n = 1;
        while (userRepository.existsByUsernameIgnoreCase(candidate)) {
            candidate = base + n++;
        }
        return candidate;
    }

    private OAuth2AuthenticationException error(String code) {
        return new OAuth2AuthenticationException(new OAuth2Error(code));
    }
}