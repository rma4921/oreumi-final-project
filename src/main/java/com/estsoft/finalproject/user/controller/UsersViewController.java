package com.estsoft.finalproject.user.controller;

import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.estsoft.finalproject.user.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UsersViewController {

    private final UsersService usersService;

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("message", "배포 테스트 성공!");
        return "test";
    }


    @GetMapping("/login")
    public String loginPage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(
            authentication.getPrincipal())) {
            if (authentication.getPrincipal() instanceof CustomUsersDetails userDetails) {
                String nickname = usersService.getOrCreateNickname(
                    userDetails.getUsers().getProvider(), userDetails.getUsername());
                model.addAttribute("nickname", nickname);
            }
        }
        return "login";
    }

    @GetMapping("/custom-login")
    public String loginTestPage() {
        return "UserTestTemplates/custom-login";
    }

    @GetMapping("/loginSuccessTest")
    public String loginSuccessPage(Model model, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String provider = oauth2Token.getAuthorizedClientRegistrationId();
            String email = oauth2User.getAttribute("email");

            model.addAttribute("email", email);
            model.addAttribute("provider", provider);
        } else if (authentication instanceof UsernamePasswordAuthenticationToken userAuth) {
            CustomUsersDetails userDetails = (CustomUsersDetails) userAuth.getPrincipal();
            String email = userDetails.getUsername();
            String provider = userDetails.getUsers().getProvider();

            model.addAttribute("email", email);
            model.addAttribute("provider", provider);
        } else {
            model.addAttribute("email", "알 수 없음");
            model.addAttribute("provider", "알 수 없음");
        }

        return "UserTestTemplates/loginSuccessTest";
    }

    @GetMapping("/another-page")
    public String anotherPage() {
        return "UserTestTemplates/another-page";
    }
}
