package com.estsoft.finalproject.user.dto;

import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String provider;
    private String email;
    private String nickname;
    private Role role;
    private LocalDateTime registerDate;
    private LocalDateTime updatedDate;

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.provider = user.getProvider();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.registerDate = user.getRegisterDate();
        this.updatedDate = user.getUpdatedDate();
    }
}
