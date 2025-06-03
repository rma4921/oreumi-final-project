package com.estsoft.finalproject.user.controller;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.estsoft.finalproject.user.dto.UsersResponse;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UsersController {
    private final UsersRepository usersRepository;

    @GetMapping("/api/users")
    public ResponseEntity<UsersResponse> getUserInfo(Authentication authentication) {
        String email = null;
        String provider = null;

        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oAuth2User = oauth2Token.getPrincipal();
            email = oAuth2User.getAttribute("email");
            provider = oauth2Token.getAuthorizedClientRegistrationId();
        } else if (authentication.getPrincipal() instanceof CustomUsersDetails userDetails) {
            email = userDetails.getUsername();
            provider = userDetails.getUsers().getProvider();
        }

        if (email == null || provider == null) {
            log.info("인증 정보가 부족합니다. authentication = {}", authentication);
            return ResponseEntity.status(401).build();
        }

        Users users = usersRepository.findByProviderAndEmail(provider, email)
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        return ResponseEntity.ok(new UsersResponse(users));
    }

}
