package com.estsoft.finalproject.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserViewController {
    // 로그인 테스트용
    @GetMapping("/custom-login")
    public String loginPage() {
        return "login";
    }

    // 로그인 성공 테스트용
    @GetMapping("/loginSuccessTest")
    public String loginSuccessPage(Model model, OAuth2AuthenticationToken authentication) {
        OAuth2User oauth2User = authentication.getPrincipal();
        String provider = authentication.getAuthorizedClientRegistrationId(); // google, naver 등
        String email = oauth2User.getAttribute("email");

        model.addAttribute("email", email);
        model.addAttribute("provider", provider);
        return "loginSuccessTest"; // templates/loginSuccessTest.html
    }
}
