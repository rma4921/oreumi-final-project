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
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class TokenTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        Field accessTokenField = JwtUtil.class.getDeclaredField("accessTokenExpirationSeconds");
        accessTokenField.setAccessible(true);
        accessTokenField.set(jwtUtil, 1L);

        Field refreshTokenField = JwtUtil.class.getDeclaredField("refreshTokenExpirationSeconds");
        refreshTokenField.setAccessible(true);
        refreshTokenField.set(jwtUtil, 60L);

        jwtUtil.init();
    }

    @Test
    void JWT_claims_검증_및_만료_테스트() throws Exception {
        String email = "test@test.com";
        String provider = "google";

        String token = jwtUtil.generateToken(email, provider);

        String extractedEmail = jwtUtil.extractEmail(token);
        String extractedProvider = jwtUtil.extractProvider(token);
        boolean expiredBefore = jwtUtil.isTokenExpired(token);

        TimeUnit.MILLISECONDS.sleep(1500);
        boolean expiredAfter = jwtUtil.isTokenExpired(token);

        assertEquals(email, extractedEmail);
        assertEquals(provider, extractedProvider);
        assertFalse(expiredBefore);
        assertTrue(expiredAfter);
    }

    @Test
    void 잘못된_토큰_형식이면_예외_발생() throws Exception {
        String invalidToken = "invalid token";

        assertThrows(JwtException.class, () -> jwtUtil.extractEmail(invalidToken));
        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }
}
