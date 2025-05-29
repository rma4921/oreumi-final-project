package com.estsoft.finalproject.user.controller;

import com.estsoft.finalproject.user.domain.User;
import com.estsoft.finalproject.user.dto.UserResponse;
import com.estsoft.finalproject.user.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/api/user")
    public ResponseEntity<UserResponse> getUserInfo(OAuth2AuthenticationToken authentication) {
        OAuth2User oauth2User = authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String provider = authentication.getAuthorizedClientRegistrationId(); // "google", "naver" 등

        log.info("email = {}", email);
        log.info("provider = {}", provider);

        User user = userRepository.findByProviderAndEmail(provider, email)
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        return ResponseEntity.ok().body(new UserResponse(user));
    }

}
