package com.estsoft.finalproject.userTest;

import static org.assertj.core.api.Assertions.*;

import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UsersTest {

    @DisplayName("user 도메인 테스트")
    @Test
    void UserDomainTest() {
        String provider = "google";
        String email = "test@test.com";
        String nickname = "test";
        Role role = Role.ROLE_USER;

        Users users = new Users(provider, email, nickname, role);

        assertThat(users.getProvider()).isEqualTo(provider);
        assertThat(users.getEmail()).isEqualTo(email);
        assertThat(users.getNickname()).isEqualTo(nickname);
        assertThat(users.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(users.getRegisterDate()).isNotNull();
    }
}
