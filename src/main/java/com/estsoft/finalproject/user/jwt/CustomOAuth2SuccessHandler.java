package com.estsoft.finalproject.user.jwt;

import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String provider = oAuth2User.getAttribute("provider");

        String token = jwtUtil.generateToken(email, provider);

        // 쿠키 생성 및 설정
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 60 * 60 = 1시간

        response.addCookie(cookie);

        log.info("JWT 쿠키 설정 완료");

        // Refresh Token 생성
        String refreshToken = jwtUtil.generateRefreshToken(email, provider);

        usersRepository.findByProviderAndEmail(provider, email).ifPresent(user -> {
            user.setRefreshToken(refreshToken);
            usersRepository.save(user);
            log.info("리프레시 토큰 DB 저장 완료");
        });

        Cookie refreshCookie = new Cookie("REFRESH", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일

        response.addCookie(refreshCookie);

        log.info("리프레시 쿠키 설정 완료");

        // 로그인 후 리디렉션
        response.sendRedirect("/loginSuccessTest");
    }
}