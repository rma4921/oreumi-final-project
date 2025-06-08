package com.estsoft.finalproject.user.config;

import com.estsoft.finalproject.user.jwt.CustomOAuth2SuccessHandler;
import com.estsoft.finalproject.user.jwt.JwtAuthenticationFilter;
import com.estsoft.finalproject.user.repository.UsersRepository;
import com.estsoft.finalproject.user.service.CustomOAuth2UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UsersService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UsersRepository usersRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user", "/loginSuccessTest").authenticated() // 인증 된 사용자만 접근 가능
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/custom-login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            exception.printStackTrace();
                            response.sendRedirect("/login?error");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // 기본 로그아웃 URL
                        .logoutSuccessHandler(new LogoutSuccessHandler() {
                            @Override
                            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
                                // Access Token 쿠키 삭제
                                Cookie jwtCookie = new Cookie("JWT", null);
                                jwtCookie.setHttpOnly(true);
                                jwtCookie.setPath("/");
                                jwtCookie.setMaxAge(0);
                                response.addCookie(jwtCookie);

                                // Refresh Token 쿠키 삭제
                                Cookie refreshCookie = new Cookie("REFRESH", null);
                                refreshCookie.setHttpOnly(true);
                                refreshCookie.setPath("/");
                                refreshCookie.setMaxAge(0);
                                response.addCookie(refreshCookie);

                                // DB의 Refresh Token 삭제
                                if (authentication != null && authentication.getPrincipal() instanceof com.estsoft.finalproject.user.dto.CustomUsersDetails userDetails) {
                                    com.estsoft.finalproject.user.domain.Users user = userDetails.getUsers();
                                    user.setRefreshToken(null);  // DB에서 refresh token 제거
                                    usersRepository.save(user);
                                    log.info("DB에서 Refresh Token 삭제 완료 - 사용자: {}", user.getEmail());
                                }

                                log.info("Spring Security 로그아웃 시 JWT, Refresh 쿠키 및 DB 토큰 삭제 완료");

                                response.sendRedirect("/custom-login?logout");
                            }
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}