package com.estsoft.finalproject.userTest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.User;
import com.estsoft.finalproject.user.dto.UserRequest;
import com.estsoft.finalproject.user.dto.UserResponse;
import com.estsoft.finalproject.user.repository.UserRepository;
import com.estsoft.finalproject.user.service.UserService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @DisplayName("회원 가입 테스트")
    @Test
    public void saveUserTest() {
        UserRequest userRequest = new UserRequest("google", "test@test.com", "test");

        Mockito.when(userRepository.save(any()))
                .thenReturn(new User(
                        userRequest.getProvider(),
                        userRequest.getEmail(),
                        userRequest.getNickname(),
                        Role.ROLE_USER
                ));

        UserResponse savedUser = userService.loginOrRegister(userRequest);

        Mockito.verify(userRepository, times(1)).save(any(User.class));
        assertThat(savedUser.getProvider()).isEqualTo(userRequest.getProvider());
        assertThat(savedUser.getEmail()).isEqualTo(userRequest.getEmail());
        assertThat(savedUser.getNickname()).isEqualTo(userRequest.getNickname());
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);

        assertThat(savedUser.getRegisterDate()).isNotNull();
        log.info("register date = " + savedUser.getRegisterDate());
        log.info("updated date = " + savedUser.getUpdatedDate());
    }

    @DisplayName("기존 유저 체크 테스트")
    @Test
    public void existUserTest() {
        UserRequest userRequest = new UserRequest("google", "test@test.com", "test");
        User existUser = new User("google", "test@test.com", "existUser", Role.ROLE_USER);

        Mockito.when(userRepository.findByProviderAndEmail("google", "test@test.com"))
                .thenReturn(Optional.of(existUser));

        UserResponse userResponse = userService.loginOrRegister(userRequest);

        Mockito.verify(userRepository, times(0)).save(any(User.class));
        assertThat(userResponse.getProvider()).isEqualTo(existUser.getProvider());
        assertThat(userResponse.getEmail()).isEqualTo(existUser.getEmail());
        assertThat(userResponse.getNickname()).isEqualTo(existUser.getNickname());
        assertThat(userResponse.getRole()).isEqualTo(existUser.getRole());
    }
}
