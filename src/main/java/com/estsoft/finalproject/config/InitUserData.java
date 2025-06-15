package com.estsoft.finalproject.config;

import com.estsoft.finalproject.comment.repository.UserRepository;
import com.estsoft.finalproject.comment.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitUserData {

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder().nickname("유령1").build());
            userRepository.save(User.builder().nickname("유령2").build());
        }
    }
}