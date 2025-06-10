package com.estsoft.finalproject.userTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.estsoft.finalproject.user.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TokenTest {
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        // 만료 시간 짧게 설정
        Field accessTokenField = JwtUtil.class.getDeclaredField("accessTokenExpirationSeconds");
        accessTokenField.setAccessible(true);
        accessTokenField.set(jwtUtil, 1L);   // 1초 유효

        Field refreshTokenField = JwtUtil.class.getDeclaredField("refreshTokenExpirationSeconds");
        refreshTokenField.setAccessible(true);
        refreshTokenField.set(jwtUtil, 60L); // 60초 유효

        jwtUtil.init(); // 키 초기화
    }

    @Test
    void JWT_claims_검증_및_만료_테스트() throws Exception {
        // given
        String email = "test@test.com";
        String provider = "google";

        String token = jwtUtil.generateToken(email, provider);

        // when
        String extractedEmail = jwtUtil.extractEmail(token);
        String extractedProvider = jwtUtil.extractProvider(token);
        boolean expiredBefore = jwtUtil.isTokenExpired(token);

        TimeUnit.MILLISECONDS.sleep(1500);  // 1.5초
        boolean expiredAfter = jwtUtil.isTokenExpired(token);

        // then
        assertEquals(email, extractedEmail);
        assertEquals(provider, extractedProvider);
        assertFalse(expiredBefore);
        assertTrue(expiredAfter);
    }

    @Test
    void 잘못된_토큰_형식이면_예외_발생() throws Exception {
        // given
        String invalidToken = "invalid token";

        // when, then
        assertThrows(JwtException.class, () -> jwtUtil.extractEmail(invalidToken));
        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }
}
