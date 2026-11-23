package com.gamecollection.security;

import com.gamecollection.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final GoogleUserService googleUserService;
    private final JwtService jwtService;
    private final String redirectUri;

    public OAuth2SuccessHandler(GoogleUserService googleUserService,
                                JwtService jwtService,
                                @Value("${app.oauth2.redirect-uri}") String redirectUri) {
        this.googleUserService = googleUserService;
        this.jwtService = jwtService;
        this.redirectUri = redirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String target;
        try {
            OidcUser googleUser = (OidcUser) authentication.getPrincipal();
            User user = googleUserService.findOrCreate(googleUser);
            target = redirectUri + "#token=" + jwtService.generateToken(user);
        } catch (OAuth2AuthenticationException e) {
            target = redirectUri + "#error=" + e.getError().getErrorCode();
        }

        // сесията трябваше само по време на разговора с Google; оттук нататък сме stateless
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(target);
    }
}