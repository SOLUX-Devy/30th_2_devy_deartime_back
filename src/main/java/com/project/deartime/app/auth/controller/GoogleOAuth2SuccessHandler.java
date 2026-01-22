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

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");

        // 요청 헤더에서 Origin 또는 Referer를 확인하여 동적으로 리다이렉트 URL 결정
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String host = request.getServerName(); // 요청 받은 서버의 도메인 확인

        String frontendUrl;
        if (origin != null && origin.contains("localhost")) {
            frontendUrl = "http://localhost:5173";
        } else if (referer != null && referer.contains("localhost")) {
            frontendUrl = "http://localhost:5173";
        } else if (host.equals("localhost") || host.equals("127.0.0.1")) {
            // Referer가 구글이어도 현재 서버가 로컬이면 로컬 프론트로 이동
            frontendUrl = "http://localhost:5173";

        } else if (referer != null && referer.contains("vercel.app")) {
            // "vercel.app"에서 왔으면 다시 거기로 보내줌(도메인 작업 이전)
            frontendUrl = "https://30th-2-devy-deartime-front.vercel.app";

        // 3. 정식 배포 환경 (새 도메인)
        } else {
            // 그 외의 모든 경우(나중에 도메인 연결되면 여기로 옴)는 정식 도메인으로
            frontendUrl = "https://deartime.kr";
        }

        String callbackUrl = frontendUrl + "/oauth/callback";

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
            System.out.println("Redirect URL: " + callbackUrl);

            String redirectUrl = String.format("%s?accessToken=%s&refreshToken=%s&email=%s&status=success",
                    callbackUrl,
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
            System.out.println("Redirect URL: " + callbackUrl);

            String redirectUrl = String.format("%s?tempToken=%s&status=signup",
                    callbackUrl,
                    URLEncoder.encode(tempToken, StandardCharsets.UTF_8)
            );

            response.sendRedirect(redirectUrl);
        }
    }
}
