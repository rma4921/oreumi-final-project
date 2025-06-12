package com.estsoft.finalproject.userTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.jwt.JwtUtil;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.http.Cookie;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class TotalTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsersRepository usersRepository;

    private Users user;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();

        user = new Users("google", "test@example.com", "tester", Role.ROLE_USER);
        usersRepository.save(user);

        accessToken = jwtUtil.generateToken(user.getEmail(), user.getProvider());
        refreshToken = jwtUtil.generateToken(user.getEmail(), user.getProvider());

        user.setRefreshToken(refreshToken);
        usersRepository.save(user);
    }

    @Test
    void 토큰_갱신_요청_시_정상_응답_확인() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/token/refresh")
                .cookie(new Cookie("REFRESH", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("JWT"))
                .andReturn();

        Cookie newAccessTokenCookie = result.getResponse().getCookie("JWT");
        assertThat(newAccessTokenCookie).isNotNull();
        assertThat(jwtUtil.isTokenValid(newAccessTokenCookie.getValue())).isTrue();
    }

    @Test
    void Refresh_Token_유지_시_새로운_Access_Token_발급() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/token/refresh")
                .cookie(new Cookie("REFRESH", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("JWT"))
                .andReturn();

        String newAccessToken = result.getResponse().getCookie("JWT").getValue();
        assertThat(jwtUtil.isTokenValid(newAccessToken)).isTrue();
    }

    @Test
    void 로그아웃_후_인증이_필요한_API_접근_시_인증되지_않는지_확인() throws Exception {
        // 로그아웃 요청
        mockMvc.perform(post("/logout")
                        .cookie(
                                new Cookie("JWT", accessToken),
                                new Cookie("REFRESH", refreshToken)
                        ))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(cookie().maxAge("JWT", 0))
                .andExpect(cookie().maxAge("REFRESH", 0));

        // DB Refresh Token 삭제 확인
        Optional<Users> optional = usersRepository.findByProviderAndEmail(user.getProvider(), user.getEmail());
        assertThat(optional).isPresent();
        assertThat(optional.get().getRefreshToken()).isNull();

        // 로그아웃 후 인증이 필요한 API 접근 시 인증 실패 확인
        mockMvc.perform(get("/loginSuccessTest"))
                .andExpect(status().is3xxRedirection());
    }
}
