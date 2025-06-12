package com.estsoft.finalproject.user.config;

import com.estsoft.finalproject.user.handler.CustomLogoutSuccessHandler;
import com.estsoft.finalproject.user.handler.CustomOAuth2FailureHandler;
import com.estsoft.finalproject.user.handler.CustomOAuth2SuccessHandler;
import com.estsoft.finalproject.user.jwt.JwtAuthenticationFilter;
import com.estsoft.finalproject.user.service.CustomOAuth2UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final CustomOAuth2UsersService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user", "/loginSuccessTest").authenticated() // 인증 된 사용자만 접근 가능
                    .requestMatchers("/mypage/**", "/api/mypage/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // 기본 로그아웃 URL
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}