package com.estsoft.finalproject.user.service;

import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UsersService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email;
        String nickname;

        Map<String, Object> userAttributes;

        switch (provider) {
            case "google" -> {
                userAttributes = attributes;
                email = (String) attributes.get("email");
                nickname = (String) attributes.get("name");
            }
            case "naver" -> {
                userAttributes = (Map<String, Object>) attributes.get("response");
                if (userAttributes == null) {
                    throw new OAuth2AuthenticationException("네이버 response 없음");
                }
                email = (String) userAttributes.get("email");
                nickname = (String) userAttributes.get("nickname");
            }
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                email = (String) kakaoAccount.get("email");
                nickname = (String) profile.get("nickname");

                Map<String, Object> modifiableAttributes = new HashMap<>(attributes);
                modifiableAttributes.put("email", email);

                userAttributes = modifiableAttributes;
            }
            default -> throw new OAuth2AuthenticationException("Unknown provider: " + provider);
        }

        Optional<Users> optionalUser = usersRepository.findByProviderAndEmail(provider, email);

        Users users = optionalUser.orElseGet(() -> {
            Users newUsers = new Users(provider, email, nickname, Role.ROLE_USER);
            return usersRepository.save(newUsers);
        });

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(users.getRole().name())),
                userAttributes,  // response 객체를 직접 attribute로 사용
                "email"          // userAttributes 안의 키 중 로그인 식별자로 사용할 것
        );
    }

}
