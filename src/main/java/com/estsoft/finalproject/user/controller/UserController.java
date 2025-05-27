package com.estsoft.finalproject.user.controller;

import com.estsoft.finalproject.user.dto.UserRequest;
import com.estsoft.finalproject.user.dto.UserResponse;
import com.estsoft.finalproject.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody UserRequest userRequest) {
        return userService.loginOrRegister(userRequest);
    }
}
