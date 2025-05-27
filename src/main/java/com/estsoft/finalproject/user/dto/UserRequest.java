package com.estsoft.finalproject.user.dto;

import com.estsoft.finalproject.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String provider;
    private String email;
    private String nickname;
}
