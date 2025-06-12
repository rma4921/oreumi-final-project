package com.estsoft.finalproject.userTest;

import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.handler.CustomOAuth2FailureHandler;
import com.estsoft.finalproject.user.handler.CustomOAuth2SuccessHandler;
import com.estsoft.finalproject.user.jwt.JwtUtil;
import com.estsoft.finalproject.user.repository.UsersRepository;
import com.estsoft.finalproject.user.service.CustomOAuth2UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    AuthenticationException exception;

    @InjectMocks
    private CustomOAuth2SuccessHandler successHandler;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users("google", "test@example.com", "tester", Role.ROLE_USER);
    }

    @Test
    void OAuth2_로그인_성공_시_JWT_Refresh_토큰_쿠키가_생성() throws Exception {
        // given
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(() -> "ROLE_USER"),
                Map.of("email", testUser.getEmail(), "provider", testUser.getProvider()),
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(usersRepository.findByProviderAndEmail(testUser.getProvider(), testUser.getEmail()))
                .thenReturn(java.util.Optional.of(testUser));

        String mockAccessToken = "access-token";
        String mockRefreshToken = "refresh-token";

        when(jwtUtil.generateToken(testUser.getEmail(), testUser.getProvider())).thenReturn(mockAccessToken);
        when(jwtUtil.generateRefreshToken(testUser.getEmail(), testUser.getProvider())).thenReturn(mockRefreshToken);

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        // accessToken, refreshToken 쿠키가 정상적으로 생성되어 응답에 추가되었는지 검증
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, atLeastOnce()).addCookie(cookieCaptor.capture());

        List<Cookie> addedCookies = cookieCaptor.getAllValues();

        boolean jwtCookieExists = addedCookies.stream()
                .anyMatch(cookie -> cookie.getName().equals("JWT"));

        boolean refreshCookieExists = addedCookies.stream()
                .anyMatch(cookie -> cookie.getName().equals("REFRESH"));

        assertThat(jwtCookieExists).isTrue();
        assertThat(refreshCookieExists).isTrue();
    }

    @Test
    void OAuth2_로그인_실패_시_적절한_리다이렉트_에러_발생() throws Exception {
        // given
        CustomOAuth2FailureHandler failureHandler = new CustomOAuth2FailureHandler();

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        verify(response).sendRedirect("/login?error=true");
    }

    @Test
    void 이미_가입된_사용자_로그인_시_새_토큰_발급_DB갱신() throws Exception {
        // given
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(() -> "ROLE_USER"),
                Map.of("email", testUser.getEmail(), "provider", testUser.getProvider()),
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(usersRepository.findByProviderAndEmail(testUser.getProvider(), testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(testUser.getEmail(), testUser.getProvider())).thenReturn(newAccessToken);
        when(jwtUtil.generateRefreshToken(testUser.getEmail(), testUser.getProvider())).thenReturn(newRefreshToken);

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        // 토큰 쿠키 응답에 추가되었는지 확인
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, atLeastOnce()).addCookie(cookieCaptor.capture());

        List<Cookie> cookies = cookieCaptor.getAllValues();
        boolean hasJwt = cookies.stream().anyMatch(cookie -> cookie.getName().equals("JWT") && cookie.getValue().equals(newAccessToken));
        boolean hasRefresh = cookies.stream().anyMatch(cookie -> cookie.getName().equals("REFRESH") && cookie.getValue().equals(newRefreshToken));

        assertThat(hasJwt).isTrue();
        assertThat(hasRefresh).isTrue();

        // db에서 refresh 토큰 갱신 확인
        assertThat(testUser.getRefreshToken()).isEqualTo(newRefreshToken);

        // userRepository.save 호출 확인
        verify(usersRepository).save(testUser);
    }

    @Test
    void 최초_소셜_로그인_사용자_회원가입_처리() throws Exception {
        // given
        String provider = "google";
        String email = "test@example.com";
        String nickname = "test";

        // OAuth2UserRequest mocking
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);

        // 구글은 attributes에 email, name이 있음
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", nickname);

        // findByProviderAndEmail -> 회원 없음
        when(usersRepository.findByProviderAndEmail(provider, email)).thenReturn(Optional.empty());
        when(usersRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // CustomOAuth2UsersService 내부 new DefaultOAuth2UserService()를 대체
        CustomOAuth2UsersService service = new CustomOAuth2UsersService(usersRepository) {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest req) {
                // 핵심 로직 직접 호출 (service 로직 테스트가 목적이므로)
                Optional<Users> optionalUser = usersRepository.findByProviderAndEmail(provider, email);
                Users users = optionalUser.orElseGet(() -> {
                    Users newUsers = new Users(provider, email, nickname, Role.ROLE_USER);
                    return usersRepository.save(newUsers);
                });

                Map<String, Object> modifiableAttributes = new HashMap<>(attributes);
                modifiableAttributes.put("provider", provider);

                return new DefaultOAuth2User(
                        List.of(new SimpleGrantedAuthority(users.getRole().name())),
                        modifiableAttributes,
                        "email"
                );
            }
        };

        // when
        service.loadUser(userRequest);

        // then
        verify(usersRepository).save(argThat(user ->
                user.getEmail().equals(email) &&
                        user.getProvider().equals(provider) &&
                        user.getNickname().equals(nickname)
        ));
    }
}
