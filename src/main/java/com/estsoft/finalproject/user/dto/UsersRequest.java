package com.estsoft.finalproject.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsersRequest {
    private String provider;
    private String email;
    private String nickname;
}
