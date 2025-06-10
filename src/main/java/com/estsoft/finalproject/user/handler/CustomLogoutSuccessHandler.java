package com.estsoft.finalproject.user.handler;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
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

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        // Access Token 쿠키 삭제
        Cookie jwtCookie = new Cookie("JWT", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        // Refresh Token 쿠키 삭제
        Cookie refreshCookie = new Cookie("REFRESH", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        // DB의 Refresh Token 삭제
        if (authentication != null && authentication.getPrincipal() instanceof CustomUsersDetails userDetails) {
            Users user = userDetails.getUsers();
            user.setRefreshToken(null);  // DB에서 refresh token 제거
            usersRepository.save(user);
            log.info("DB에서 Refresh Token 삭제 완료 - 사용자: {}", user.getEmail());
        }

        log.info("Spring Security 로그아웃 시 JWT, Refresh 쿠키 및 DB 토큰 삭제 완료");

        response.sendRedirect("/custom-login?logout");
    }
}
