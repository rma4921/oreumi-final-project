package com.estsoft.finalproject.Post.controller;

import com.estsoft.finalproject.Post.dto.ScrapPostResponseDto;
import com.estsoft.finalproject.Post.service.ScrapPostService;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/post")
    public String getScrapPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String keyword,
        Model model
    ) {
        int pageSize = 10;
        int blockSize = 10;

        Pageable pageable = PageRequest.of(page, pageSize,
            Sort.by(Direction.DESC, "postDate"));
        Page<ScrapPostResponseDto> postPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            postPage = scrapPostService.getAllPosts(pageable);
        } else {
            postPage = scrapPostService.getAllPostsByTitle(keyword, pageable);
        }

        int currentPage = postPage.getNumber();
        int totalPages = postPage.getTotalPages();

        int startPage = (currentPage / blockSize) * blockSize;
        int endPage = Math.min(currentPage + blockSize - 1, totalPages - 1);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasPrevBlock", startPage > 0);
        model.addAttribute("hasNextBlock", endPage < totalPages - 1);
        model.addAttribute("keyword", keyword);

        return "layout/post/post";
    }

}
