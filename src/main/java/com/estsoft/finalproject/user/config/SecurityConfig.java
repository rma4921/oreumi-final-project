package com.estsoft.finalproject.user.config;

import com.estsoft.finalproject.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/oauth2/**", "/loginSuccessTest.html", "/custom-login").permitAll()   // 인증 없이 접근 가능한 경로
                        .requestMatchers("/user").authenticated()   // 인증 필요한 엔드포인트
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/custom-login") // 나중에 login으로 변경
                        .defaultSuccessUrl("/loginSuccessTest", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // 로그인 실패 에러 추적
                        .failureHandler((request, response, exception) -> {
                            exception.printStackTrace(); // 콘솔에서 에러 추적
                            response.sendRedirect("/login?error");
                        })
                );

        return http.build();
    }
}
