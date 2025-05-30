package com.estsoft.finalproject.user.jwt;

import com.estsoft.finalproject.user.service.CustomUsersDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// jwt 토큰을 검증해 사용자 인증 처리 하는 필터
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUsersDetailService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUsersDetailService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("[JwtAuthenticationFilter] 필터 실행됨 - 요청 URI: {}", request.getRequestURI());

        // 이미 인증 정보가 있으면 JWT 인증 건너뛰기
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            log.info("이미 인증된 사용자 존재, JWT 인증 필터 통과");
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                log.info("쿠키 발견 - 이름: {}, 값: {}", cookie.getName(), cookie.getValue());
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    log.info("JWT 토큰 감지: {}", token);
                }
            }
        } else {
            log.info("쿠키 없음");
        }

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmail(token);
            log.info("유효한 JWT. 사용자 이메일 : {}", email);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("인증 객체 설정 완료 : {}", userDetails.getUsername());
        } else if (token != null) {
            log.info("유효하지 않은 JWT 토큰");
        }

        filterChain.doFilter(request, response);
    }

}
