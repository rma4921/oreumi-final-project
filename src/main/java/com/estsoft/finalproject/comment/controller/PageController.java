package com.estsoft.finalproject.comment.controller;

import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    @GetMapping("/posts/{postId}")
    public String showCommentPage(@PathVariable Long postId,
                                  Model model,
                                  @AuthenticationPrincipal CustomUsersDetails userDetails) {
        model.addAttribute("postId", postId);

        if (userDetails != null) {
            model.addAttribute("user", userDetails.getUser());
            model.addAttribute("userId", userDetails.getUser().getUserId());
        }

        return "postDetail";
    }
}