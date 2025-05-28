package com.estsoft.finalproject.user.service;

import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.User;
import com.estsoft.finalproject.user.dto.UserRequest;
import com.estsoft.finalproject.user.dto.UserResponse;
import com.estsoft.finalproject.user.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
