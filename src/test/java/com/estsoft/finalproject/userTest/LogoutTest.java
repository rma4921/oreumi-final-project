package com.estsoft.finalproject.userTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.estsoft.finalproject.user.handler.CustomLogoutSuccessHandler;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class LogoutTest {
    @Mock
    private UsersRepository usersRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUsersDetails customUsersDetails;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    private CustomLogoutSuccessHandler logoutSuccessHandler;

    @BeforeEach
    void setUp() {
        logoutSuccessHandler = new CustomLogoutSuccessHandler(usersRepository);
    }

    @Test
    void 로그아웃_요청_시_JWT_REFRESH_쿠키_삭제() throws Exception {
        // given
        when(authentication.getPrincipal()).thenReturn(customUsersDetails);
        Users users = new Users();
        users.setEmail("test@test.com");
        users.setRefreshToken("refresh");

        when(customUsersDetails.getUsers()).thenReturn(users);

        // when
        logoutSuccessHandler.onLogoutSuccess(request, response, authentication);

        // then
        verify(response, times(2)).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getAllValues()).anySatisfy(cookie -> {
            assertThat(cookie.getName()).isIn("JWT", "REFRESH");
            assertThat(cookie.getValue()).isNull();
            assertThat(cookie.getMaxAge()).isEqualTo(0);
        });
    }

    @Test
    void 로그아웃_시_DB에_저장_된_REFRESH_삭제() throws Exception {
        // given
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setRefreshToken("refresh");

        when(authentication.getPrincipal()).thenReturn(customUsersDetails);
        when(customUsersDetails.getUsers()).thenReturn(user);

        // when
        logoutSuccessHandler.onLogoutSuccess(request, response, authentication);

        // then
        assertThat(user.getRefreshToken()).isNull();
        verify(usersRepository).save(user);
    }
}
