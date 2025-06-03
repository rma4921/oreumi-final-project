package com.estsoft.finalproject.user.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private SecretKey secretKey;
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1000 * 60 * 60 = 1시간

    @PostConstruct
    public void init() {
        // 안전한 키를 생성합니다
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        log.info("JWT 키 초기화 완료");
    }

    // JWT 생성
    public String generateToken(String email, String provider) {
        log.info("JWT 토큰 생성 중");

        String token = Jwts.builder()
                .setSubject(email)
                .claim("provider", provider)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();

        log.info("JWT 토큰 생성 완료: {}", token);
        return token;
    }

    // JWT 검증 및 이메일 추출
    public String extractEmail(String token) {
        try {
            // "Bearer " 접두사 제거 (있을 경우만)
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            log.info("JWT 토큰이 만료되었습니다.");
            throw e;
        } catch (JwtException e) {
            log.info("JWT 토큰이 유효하지 않습니다.");
            throw e;
        }
    }

    // JWT 에서 provider 추출
    public String extractProvider(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("provider", String.class);
    }
}
