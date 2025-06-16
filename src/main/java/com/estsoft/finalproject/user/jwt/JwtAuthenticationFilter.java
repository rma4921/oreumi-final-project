package com.estsoft.finalproject.user.jwt;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    @Value("${jwt.cookieExpirationSeconds}")
    private int cookieExpirationSeconds;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        String jwt = null;

        if (request.getCookies() != null) {
            jwt = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("JWT"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        }

        if (jwt == null) {
            log.info("JWT 토큰이 없습니다.");
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = jwtUtil.extractEmail(jwt);
            String provider = jwtUtil.extractProvider(jwt);

            if (jwtUtil.isTokenExpired(jwt)) {
                log.info("Access Token이 만료됨. Refresh Token으로 재발급 시도.");

                String refreshToken = null;
                if (request.getCookies() != null) {
                    refreshToken = Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals("REFRESH"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null);
                }

                if (refreshToken != null && jwtUtil.isTokenValid(refreshToken)) {
                    String refreshEmail = jwtUtil.extractEmail(refreshToken);
                    String refreshProvider = jwtUtil.extractProvider(refreshToken);

                    Users user = usersRepository.findByProviderAndEmail(refreshProvider,
                            refreshEmail)
                        .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));

                    String newAccessToken = jwtUtil.generateToken(refreshEmail, refreshProvider);

                    Cookie newCookie = new Cookie("JWT", newAccessToken);
                    newCookie.setHttpOnly(true);
                    newCookie.setPath("/");
                    newCookie.setMaxAge(cookieExpirationSeconds);
                    response.addCookie(newCookie);

                    CustomUsersDetails userDetails = new CustomUsersDetails(user);
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
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

            Users user = usersRepository.findByProviderAndEmail(provider, email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));
            CustomUsersDetails userDetails = new CustomUsersDetails(user);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("JWT 인증 성공: {}", email);

        } catch (Exception e) {
            log.warn("JWT 인증 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
