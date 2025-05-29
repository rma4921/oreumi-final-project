package com.estsoft.finalproject.Post.controller;

import com.estsoft.finalproject.Post.service.ScrapPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ScrapPostController {

    private final ScrapPostService scrapPostService;

    @PostMapping("/api/mypage/{scrapId}")
    public String saveScrapPost(@PathVariable Long scrapId) {
        scrapPostService.savePost(scrapId);

        return "redirect:/mypage/scrap/" + scrapId;
    }

}
