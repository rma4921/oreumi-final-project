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

    public UserResponse loginOrRegister(UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findByProviderAndEmail(userRequest.getProvider(), userRequest.getEmail());

        User user = optionalUser.orElseGet(() -> {
            User newUser = new User(userRequest.getProvider(), userRequest.getEmail(), userRequest.getNickname(), Role.ROLE_USER);
            return userRepository.save(newUser);
        });

        return new UserResponse(user);
    }
}
