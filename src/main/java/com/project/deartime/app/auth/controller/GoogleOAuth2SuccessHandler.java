package com.project.deartime.app.auth.controller;

import com.project.deartime.app.auth.repository.UserRepository;
import com.project.deartime.app.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String FRONTEND_CALLBACK_URL = "https://30th-2-devy-deartime-front.vercel.app/oauth/callback";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");

        var optionalUser = userRepository.findByProviderId(providerId);

        if (optionalUser.isPresent() && optionalUser.get().isRegistered()) {
            // 기존 유저 - 정식 토큰 발급
            User user = optionalUser.get();
            String accessToken = jwtTokenProvider.createAccessToken(user.getId().toString(), user.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getId().toString());

            System.out.println("=== 로그인 성공 ===");
            System.out.println("Access Token: " + accessToken);
            System.out.println("Refresh Token: " + refreshToken);
            System.out.println("Email: " + user.getEmail());

            String redirectUrl = String.format("%s?accessToken=%s&refreshToken=%s&email=%s&status=success",
                    FRONTEND_CALLBACK_URL,
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
            );

            response.sendRedirect(redirectUrl);

        } else {
            // 신규 유저 - 임시 토큰 발급 (회원가입용)
            String tempToken = jwtTokenProvider.createTempToken(providerId, email);

            System.out.println("=== 신규 유저 - 임시 토큰 발급 ===");
            System.out.println("Temp Token: " + tempToken);

            String redirectUrl = String.format("%s?tempToken=%s&status=signup",
                    FRONTEND_CALLBACK_URL,
                    URLEncoder.encode(tempToken, StandardCharsets.UTF_8)
            );

            response.sendRedirect(redirectUrl);
        }
    }
}
