package com.estsoft.finalproject.comment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/posts/{postId}")
    public String showCommentPage() {
        return "postDetail";
    }
}