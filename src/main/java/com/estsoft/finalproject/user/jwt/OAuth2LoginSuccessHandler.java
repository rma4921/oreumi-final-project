package com.estsoft.finalproject.user.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

// OAuth2 로그인 성공 시 jwt 생성 후 쿠키에 저장하고 응답하는 핸들러
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = authentication.getName();
        String token = jwtTokenProvider.createToken(email);

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 1시간
        cookie.setSecure(true); // HTTPS 환경에서만 전송, 배포환경에서는 true 권장
//        cookie.setDomain("도메인");  // 도메인 설정

        response.addCookie(cookie);
        response.sendRedirect("/loginSuccessTest");
    }
}
