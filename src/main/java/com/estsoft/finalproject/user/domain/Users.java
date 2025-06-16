package com.estsoft.finalproject.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String email;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;

    @CreatedDate
    @Column(name = "register_date", nullable = false, updatable = false)
    private LocalDateTime registerDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "refresh_token")
    private String refreshToken;

    public Users(String provider, String email, String nickname, Role role) {
        this.provider = provider;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.registerDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    public void updateId(Long userId) {
        this.userId = userId;
    }
}
