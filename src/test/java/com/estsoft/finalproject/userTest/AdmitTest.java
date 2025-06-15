package com.estsoft.finalproject.userTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.jwt.JwtUtil;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AdmitTest {
    @InjectMocks
    TestableJwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    UsersRepository usersRepository;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @Mock
    FilterChain filterChain;

    Users user;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        user = new Users("google", "test@example.com", "test", Role.ROLE_USER);
        user.setRefreshToken("refresh-token");
        SecurityContextHolder.clearContext();
    }

    @Test
    void 유효한_Access_Token이_있을_때_정상_인증_처리() throws Exception {
        // given
        String jwt = "test-jwt";

        request.setCookies(new Cookie("JWT", jwt));

        when(jwtUtil.extractEmail(jwt)).thenReturn(user.getEmail());
        when(jwtUtil.extractProvider(jwt)).thenReturn(user.getProvider());
        when(jwtUtil.isTokenExpired(jwt)).thenReturn(false);
        when(usersRepository.findByProviderAndEmail(user.getProvider(), user.getEmail()))
                .thenReturn(Optional.of(user));

        // when
        jwtAuthenticationFilter.testDoFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(user.getEmail(), auth.getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void Access_Token_만료되고_Refresh_Token이_유효할_경우_Access_Token_자동_재발급() throws Exception {
        // given
        String expiredJwt = "expired-jwt";
        String refreshToken = "refresh-token";
        String newAccessToken = "new-accessToken";

        request.setCookies(
                new Cookie("JWT", expiredJwt),
                new Cookie("REFRESH", refreshToken)
        );

        // Access Token 만료
        when(jwtUtil.extractEmail(expiredJwt)).thenReturn(user.getEmail());
        when(jwtUtil.extractProvider(expiredJwt)).thenReturn(user.getProvider());
        when(jwtUtil.isTokenExpired(expiredJwt)).thenReturn(true);

        // Refresh Token 유효
        when(jwtUtil.isTokenValid(refreshToken)).thenReturn(true);
        when(jwtUtil.extractEmail(refreshToken)).thenReturn(user.getEmail());
        when(jwtUtil.extractProvider(refreshToken)).thenReturn(user.getProvider());
        when(usersRepository.findByProviderAndEmail(user.getProvider(), user.getEmail()))
                .thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user.getEmail(), user.getProvider()))
                .thenReturn(newAccessToken);

        // when
        jwtAuthenticationFilter.testDoFilterInternal(request, response, filterChain);

        // then
        // 새 쿠키 발급 확인
        boolean hasNewAccessCookie = Arrays.stream(response.getCookies())
                .anyMatch(cookie -> cookie.getName().equals("JWT") && cookie.getValue().equals(newAccessToken));
        assertTrue(hasNewAccessCookie);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(user.getEmail(), auth.getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void Refresh_Token이_유효하지_않을_경우_인증_실패_처리() throws Exception {
        // given
        String expiredJwt = "expired-jwt";
        String invalidToken = "invalid-refresh";

        request.setCookies(
                new Cookie("JWT", expiredJwt),
                new Cookie("REFRESH", invalidToken)
        );

        when(jwtUtil.extractEmail(expiredJwt)).thenReturn(user.getEmail());
        when(jwtUtil.extractProvider(expiredJwt)).thenReturn(user.getProvider());
        when(jwtUtil.isTokenExpired(expiredJwt)).thenReturn(true);
        when(jwtUtil.isTokenValid(invalidToken)).thenReturn(false);

        // when
        jwtAuthenticationFilter.testDoFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 쿠키_없을_경우_접근_차단() throws Exception {
        // given(쿠키 없을 경우 테스트 이므로 given 없음)

        // when
        jwtAuthenticationFilter.testDoFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 유효하지_않은_JWT가_있을_경우_필터에서_예외_처리() throws Exception {
        // given
        String invalidJwt = "invalid-jwt";
        request.setCookies(new Cookie("JWT", invalidJwt));

        when(jwtUtil.extractEmail(invalidJwt)).thenThrow(new RuntimeException("유효하지 않은 토큰"));

        // when
        jwtAuthenticationFilter.testDoFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

}
