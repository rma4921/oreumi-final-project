package com.estsoft.finalproject.user.jwt;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 인증이 필요한 경로 목록
        List<String> authRequiredPaths = List.of("/user", "/loginSuccessTest");

        String requestURI = request.getRequestURI();

        boolean requiresAuth = authRequiredPaths.stream()
                .anyMatch(path -> {
                    if (path.endsWith("/**")) {
                        String prefix = path.substring(0, path.length() - 3);
                        return requestURI.startsWith(prefix);
                    } else {
                        return requestURI.equals(path);
                    }
                });

        if (!requiresAuth) {
            // 인증이 필요 없는 경로는 토큰 없이 그냥 통과
            log.info("JWT 인증이 필요없는 경로");
            filterChain.doFilter(request, response);
            return;
        }

        // 인증 필요 경로라면 JWT 검사 시작
        String jwt = null;

        if (request.getCookies() != null) {
            jwt = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("JWT"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        if (jwt == null) {
            // JWT 토큰 없으면 401 반환
            log.info("JWT 토큰이 없습니다.");
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: JWT token is missing");
            return;
        }

        try {
            String email = jwtUtil.extractEmail(jwt);
            String provider = jwtUtil.extractProvider(jwt);

            Users user = usersRepository.findByProviderAndEmail(provider, email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));

            CustomUsersDetails userDetails = new CustomUsersDetails(user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("JWT 인증 성공: {}", email);

        } catch (Exception e) {
            log.info("JWT 인증 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

}
