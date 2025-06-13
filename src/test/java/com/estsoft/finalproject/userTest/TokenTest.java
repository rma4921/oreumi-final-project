package com.estsoft.finalproject.userTest;

import static org.junit.jupiter.api.Assertions.*;

import com.estsoft.finalproject.user.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TokenTest {

    @Autowired
    private JwtUtil jwtUtil;

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

        TimeUnit.MILLISECONDS.sleep(1500);  // 1.5초 기다려서 토큰 만료 시킴
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
