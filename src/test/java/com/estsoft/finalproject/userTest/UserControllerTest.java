package com.estsoft.finalproject.userTest;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.estsoft.finalproject.user.controller.UserController;
import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.dto.UserResponse;
import com.estsoft.finalproject.user.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @DisplayName("user 컨트롤러 회원가입 테스트")
    @Test
    public void saveUserTest() throws Exception {
        String jsonUser = """
                {
                    "provider" : "google",
                    "email" : "test@test.com",
                    "nickname" : "test"
                }
                """;

        Mockito.when(userService.loginOrRegister(any()))
                .thenReturn(new UserResponse(
                        1L,
                        "google",
                        "test@test.com",
                        "test",
                        Role.ROLE_USER,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ));

        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.provider").value("google"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.nickname").value("test"));
    }
}
