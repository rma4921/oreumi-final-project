package com.estsoft.finalproject.mypage.controller;

import com.estsoft.finalproject.mypage.service.ScrappedArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ScrappedArticleController {

    private final ScrappedArticleService scrappedArticleService;

    @DeleteMapping("/api/mypage/{scrapId}")
    public String deleteScrappedArticle(@PathVariable Long scrapId) {
        scrappedArticleService.deleteScrappedArticle(scrapId);

        return "redirect:/mypage";
    }

}
