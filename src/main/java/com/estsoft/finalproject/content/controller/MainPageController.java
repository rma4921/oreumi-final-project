package com.estsoft.finalproject.content.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {
    @GetMapping("/home")
    public String showMainPage() {
        return "mainPage";
    }
}
