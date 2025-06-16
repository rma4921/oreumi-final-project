package com.estsoft.finalproject.user.controller;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.jwt.JwtUtil;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    @PostMapping("/api/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request,
        HttpServletResponse response) {
        String refreshToken = null;

        if (request.getCookies() != null) {
            refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("REFRESH"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        }

        if (refreshToken == null) {
            log.info("리프레시 토큰이 없습니다.");
            return ResponseEntity.status(401).body("Refresh token is missing");
        }

        try {
            if (!jwtUtil.isTokenValid(refreshToken)) {
                log.info("리프레시 토큰이 유효하지 않습니다.");
                return ResponseEntity.status(401).body("Invalid refresh token");
            }

            String email = jwtUtil.extractEmail(refreshToken);
            String provider = jwtUtil.extractProvider(refreshToken);

            Users user = usersRepository.findByProviderAndEmail(provider, email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

            String newAccessToken = jwtUtil.generateToken(email, provider);

            Cookie newAccessCookie = new Cookie("JWT", newAccessToken);
            newAccessCookie.setHttpOnly(true);
            newAccessCookie.setPath("/");
            newAccessCookie.setMaxAge(60 * 60);

            response.addCookie(newAccessCookie);

            log.info("새로운 액세스 토큰 발급 및 쿠키 저장 완료: 이메일 = {}, 프로바이더 = {}", email, provider);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("리프레시 토큰 처리 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }
}
