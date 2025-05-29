package com.estsoft.finalproject.mypage.controller;

import com.estsoft.finalproject.mypage.dto.RelatedStockResponseDTO;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleDetailResponseDto;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
import com.estsoft.finalproject.mypage.service.RelatedStockService;
import com.estsoft.finalproject.mypage.service.ScrappedArticleService;
import com.estsoft.finalproject.security.UserDetail;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final ScrappedArticleService scrappedArticleService;
    private final RelatedStockService relatedStockService;

    @GetMapping("/mypage")
    public String getScrappedArticles(
        @AuthenticationPrincipal UserDetail userDetail,
        @RequestParam(defaultValue = "0") int scrapPage,
        @RequestParam(required = false) String keyword,
        Model model
    ) {
        int pageSize = 10;
        int blockSize = 10;

        Pageable pageable = PageRequest.of(scrapPage, pageSize,
            Sort.by(Direction.DESC, "scrapDate"));
        Page<ScrappedArticleResponseDto> page;

        if (keyword == null || keyword.trim().isEmpty()) {
            page = scrappedArticleService.getScrappedArticlesByUser(userDetail.getUser(), pageable);
        } else {
            page = scrappedArticleService.getScrappedArticlesByUserAndTitle(userDetail.getUser(), keyword,
                pageable);
        }

        int currentPage = page.getNumber();
        int totalPages = page.getTotalPages();

        int startPage = (currentPage / blockSize) * blockSize;
        int endPage = Math.min(currentPage + blockSize - 1, totalPages - 1);

        model.addAttribute("scrappedArticle", page.getContent());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasPrevBlock", startPage > 0);
        model.addAttribute("hasNextBlock", endPage < totalPages - 1);
        model.addAttribute("keyword", keyword);

        return "layout/mypage/mypage";
    }

    @GetMapping("/mypage/scrap/{scrapId}")
    public String getScrappedArticleDetail(@PathVariable Long scrapId, Model model) {
        ScrappedArticleDetailResponseDto detailDto = scrappedArticleService.getScrappedArticleDetail(
            scrapId);
        List<RelatedStockResponseDTO> relatedStocks = relatedStockService.findByScrapId(scrapId);

        model.addAttribute("scrappedArticle", detailDto);
        model.addAttribute("relatedStocks", relatedStocks);

        return "layout/mypage/detail";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "/layout/login/login";
    }

}



