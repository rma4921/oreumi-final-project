package com.estsoft.finalproject.user.handler;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.estsoft.finalproject.user.jwt.JwtUtil;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException {
        Cookie jwtCookie = new Cookie("JWT", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        Cookie refreshCookie = new Cookie("REFRESH", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        if (authentication != null
            && authentication.getPrincipal() instanceof CustomUsersDetails userDetails) {
            Users user = userDetails.getUsers();
            user.setRefreshToken(null);
            usersRepository.save(user);
            log.info("DB에서 Refresh Token 삭제 완료 - 사용자: {}", user.getEmail());
        } else {
            String jwt = extractCookie(request, "JWT");
            if (jwt != null && jwtUtil.isTokenValid(jwt)) {
                String email = jwtUtil.extractEmail(jwt);
                String provider = jwtUtil.extractProvider(jwt);
                usersRepository.findByProviderAndEmail(provider, email)
                    .ifPresent(user -> {
                        user.setRefreshToken(null);
                        usersRepository.save(user);
                        log.info("비인증 상태에서 DB Refresh Token 삭제 - 사용자: {}", user.getEmail());
                    });
            }
        }

        log.info("Spring Security 로그아웃 처리 완료 - 쿠키 제거 및 토큰 정리");
        response.sendRedirect("/home");
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
