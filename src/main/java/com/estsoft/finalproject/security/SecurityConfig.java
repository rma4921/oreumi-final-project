package com.estsoft.finalproject.security;

import com.estsoft.finalproject.user.User;
import com.estsoft.finalproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;

    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring().requestMatchers("/static/**")
            .requestMatchers("/css/**", "/js/**", "/images/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
            auth -> auth.requestMatchers("/login", "/signup", "/user").permitAll()
                .requestMatchers("/api/user/signup", "/logout").permitAll()
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("mypage/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/mypage", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
            )
            .csrf(auth -> auth.disable());
        return httpSecurity.build();
    }

    @Bean // 테스트용 하드 코딩
    public UserDetailsService userDetailsService() {
        return username -> {
            // test 계정만 허용
            if (!username.equals("테스트유저1")) {
                throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
            }

            User user = userRepository.findById(1L)
                .orElseThrow(() -> new UsernameNotFoundException("user_id=1 유저가 없습니다."));

            return new UserDetail(user);
        };
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
