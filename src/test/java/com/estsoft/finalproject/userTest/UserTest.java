package com.estsoft.finalproject.userTest;

import static org.assertj.core.api.Assertions.*;

import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

    @DisplayName("user 도메인 테스트")
    @Test
    void UserDomainTest() {
        String provider = "google";
        String email = "test@test.com";
        String nickname = "test";
        Role role = Role.ROLE_USER;

        User user = new User(provider, email, nickname, role);

        assertThat(user.getProvider()).isEqualTo(provider);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(user.getRegisterDate()).isNotNull();
    }
}
