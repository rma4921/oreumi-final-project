package com.estsoft.finalproject.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

// 동엽님이랑 맞춰봐야 함.
@Entity
@Table(name = "user")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String nickname;

    // 테스트 코드용
    public void updateId(Long id) {
        this.id = id;
    }

    // 테스트 코드용
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

}
