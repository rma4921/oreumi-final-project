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
    private final long EXPIRATION_TIME = 1000 * 60; // 1000 * 60 * 60 = 1시간
    private final long REFRESH_TIME = 1000 * 60 * 60 * 24 * 7;

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

    // Refresh Token 생성
    public String generateRefreshToken(String email, String provider) {
        return Jwts.builder()
                .setSubject(email)
                .claim("provider", provider)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
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

    // 토큰 유효성 검사
    public boolean isTokenValid(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            log.info("토큰이 만료되었습니다.");
        } catch (JwtException e) {
            log.info("유효하지 않은 토큰입니다.");
        } catch (Exception e) {
            log.info("토큰 검사 중 알 수 없는 오류 발생: {}", e.getMessage());
        }
        return false;
    }

    // 토큰 만료 여부
    public boolean isTokenExpired(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // 명확히 만료됨
        } catch (Exception e) {
            return false; // 유효하지 않거나 다른 이유 → 여기선 false로 처리 (상황에 따라 바꿀 수 있음)
        }
    }
}
