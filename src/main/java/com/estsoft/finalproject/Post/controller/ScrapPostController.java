package com.estsoft.finalproject.Post.controller;

import com.estsoft.finalproject.Post.service.ScrapPostService;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ScrapPostController {

    private final ScrapPostService scrapPostService;

    @PostMapping("/api/mypage/{scrapId}")
    public String saveScrapPost(@PathVariable Long scrapId,
        @AuthenticationPrincipal CustomUsersDetails userDetail) {

        scrapPostService.savePost(scrapId, userDetail.getUser());

        return "redirect:/mypage/scrap/" + scrapId;
    }

}
