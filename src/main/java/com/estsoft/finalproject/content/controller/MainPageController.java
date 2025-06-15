package com.estsoft.finalproject.content.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainPageController {
    @GetMapping("/home")
    public String showMainPage() {
        return "mainPage";
    }

    @GetMapping("/news/detail")
    public String showDetail(@RequestParam(name = "news-url") String newsId) {

        return "newsDetail";
    }
}
