package com.estsoft.finalproject.user.jwt;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 모든 요청에 대해 JWT 검사 시작
        String jwt = null;

        // 쿠키에서 JWT 추출
        if (request.getCookies() != null) {
            jwt = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("JWT"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        // JWT가 없는 경우 → 인증 없이 계속 진행
        if (jwt == null) {
            log.info("JWT 토큰이 없습니다.");
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 이메일 및 provider 추출
            String email = jwtUtil.extractEmail(jwt);
            String provider = jwtUtil.extractProvider(jwt);

            // Access Token 만료되었는지 확인
            if (jwtUtil.isTokenExpired(jwt)) {
                log.info("Access Token이 만료됨. Refresh Token으로 재발급 시도.");

                // Refresh 토큰 꺼내기
                String refreshToken = null;
                if (request.getCookies() != null) {
                    refreshToken = Arrays.stream(request.getCookies())
                            .filter(cookie -> cookie.getName().equals("Refresh"))
                            .findFirst()
                            .map(Cookie::getValue)
                            .orElse(null);
                }

                if (refreshToken != null && jwtUtil.isTokenValid(refreshToken)) {
                    String refreshEmail = jwtUtil.extractEmail(refreshToken);
                    String refreshProvider = jwtUtil.extractProvider(refreshToken);

                    Users user = usersRepository.findByProviderAndEmail(refreshProvider, refreshEmail)
                            .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));

                    // 새 Access Token 발급
                    String newAccessToken = jwtUtil.generateToken(refreshEmail, refreshProvider);

                    // 쿠키로 다시 설정
                    Cookie newCookie = new Cookie("JWT", newAccessToken);
                    newCookie.setHttpOnly(true);
                    newCookie.setPath("/");
                    newCookie.setMaxAge(60 * 60); // 1시간
                    response.addCookie(newCookie);

                    // SecurityContext 설정
                    CustomUsersDetails userDetails = new CustomUsersDetails(user);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("리프레시 토큰으로 Access Token 재발급 성공");

                    filterChain.doFilter(request, response);
                    return;
                } else {
                    log.warn("리프레시 토큰 없음 또는 유효하지 않음");
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            // 기존 Access Token이 유효한 경우
            Users user = usersRepository.findByProviderAndEmail(provider, email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));
            CustomUsersDetails userDetails = new CustomUsersDetails(user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("JWT 인증 성공: {}", email);

        } catch (Exception e) {
            log.warn("JWT 인증 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void redirectToLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/custom-login");
    }
}
