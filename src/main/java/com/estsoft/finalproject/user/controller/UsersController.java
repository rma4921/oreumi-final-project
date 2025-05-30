package com.estsoft.finalproject.user.controller;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.UsersResponse;
import com.estsoft.finalproject.user.repository.UsersRepository;
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
public class UsersController {
    private final UsersRepository usersRepository;

    @GetMapping("/api/users")
    public ResponseEntity<UsersResponse> getUserInfo(OAuth2AuthenticationToken authentication) {
        OAuth2User oauth2User = authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String provider = authentication.getAuthorizedClientRegistrationId(); // "google", "naver" 등

        log.info("email = {}", email);
        log.info("provider = {}", provider);

        Users users = usersRepository.findByProviderAndEmail(provider, email)
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        return ResponseEntity.ok().body(new UsersResponse(users));
    }

}
