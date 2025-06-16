package com.estsoft.finalproject.user.dto;

import com.estsoft.finalproject.user.domain.Users;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponse {

    private Long userId;
    private String provider;
    private String email;
    private String nickname;
    private String role;
    private LocalDateTime registerDate;
    private LocalDateTime updatedDate;

    public UsersResponse(Users users) {
        this.userId = users.getUserId();
        this.provider = users.getProvider();
        this.email = users.getEmail();
        this.nickname = users.getNickname();
        this.role = users.getRole().name();
        this.registerDate = users.getRegisterDate();
        this.updatedDate = users.getUpdatedDate();
    }
}
