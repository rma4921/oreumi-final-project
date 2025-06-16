package com.estsoft.finalproject.mypage.controller;

import com.estsoft.finalproject.category.dto.CategoryResponseDto;
import com.estsoft.finalproject.comment.dto.CommentResponseDto;
import com.estsoft.finalproject.comment.service.CommentService;
import com.estsoft.finalproject.mypage.dto.RelatedStockResponseDTO;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleDetailResponseDto;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
import com.estsoft.finalproject.mypage.service.RelatedStockService;
import com.estsoft.finalproject.mypage.service.ScrappedArticleService;
import com.estsoft.finalproject.category.service.CategoryService;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final CategoryService categoryService;
    private final CommentService commentService;

    @GetMapping("/mypage")
    public String getScrappedArticles(
        @AuthenticationPrincipal CustomUsersDetails userDetail,
        @RequestParam(defaultValue = "0") int scrapPage,
        @RequestParam(defaultValue = "scrap") String tab,
        @RequestParam(defaultValue = "0") int commentPage,
        @RequestParam(required = false) String keyword,
        Model model
    ) {
        int pageSize = 10;
        int blockSize = 10;
        int currentPage, totalPages, startPage, endPage;

        if ("scrap".equals(tab)) {
            Map<Long, List<String>> articlesCategory = new HashMap<>();

            Pageable pageable = PageRequest.of(scrapPage, pageSize,
                Sort.by(Direction.DESC, "scrapDate"));
            Page<ScrappedArticleResponseDto> page;

            if (keyword == null || keyword.trim().isEmpty()) {
                page = scrappedArticleService.getScrappedArticlesByUser(userDetail.getUsers(), pageable);
            } else {
                page = scrappedArticleService.getScrappedArticlesByUserAndTitle(userDetail.getUsers(),
                    keyword, pageable);
            }
            currentPage = page.getNumber();
            totalPages = page.getTotalPages();

            startPage = (currentPage / blockSize) * blockSize;
            endPage = Math.min(currentPage + blockSize - 1, totalPages - 1);

            for (ScrappedArticleResponseDto article : page.getContent()) {
                Long scrapId = article.getScrapId();

                List<CategoryResponseDto> articleCategories = categoryService.getCategoriesByScrapId(
                    scrapId);

                List<String> categoryNames = articleCategories.stream()
                    .map(CategoryResponseDto::getCategoryName)
                    .toList();

                articlesCategory.put(scrapId, categoryNames);
            }

            model.addAttribute("scrappedArticle", page.getContent());
            model.addAttribute("articlesCategory", articlesCategory);
        } else {
            Pageable pageable = PageRequest.of(commentPage, pageSize,
                Sort.by(Direction.DESC, "createTime"));
            Page<CommentResponseDto> page;

            if (keyword == null || keyword.trim().isEmpty()) {
                page = commentService.getCommentsByUser(userDetail.getUsers(), pageable);
            } else {
                page = commentService.findByUserAndContentContaining(userDetail.getUsers(),
                    keyword, pageable);
            }
            currentPage = page.getNumber();
            totalPages = page.getTotalPages();

            startPage = (currentPage / blockSize) * blockSize;
            endPage = Math.min(currentPage + blockSize - 1, totalPages - 1);

            model.addAttribute("comments", page.getContent());
        }

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasPrevBlock", startPage > 0);
        model.addAttribute("hasNextBlock", endPage < totalPages - 1);
        model.addAttribute("keyword", keyword);
        model.addAttribute("tab", tab);

        return "layout/mypage/mypage";
    }

    @GetMapping("/mypage/scrap/{scrapId}")
    public String getScrappedArticleDetail(@PathVariable Long scrapId, Model model) {
        ScrappedArticleDetailResponseDto detailDto = scrappedArticleService.getScrappedArticleDetail(
            scrapId);
        List<RelatedStockResponseDTO> relatedStocks = relatedStockService.findByScrapId(scrapId);
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        List<CategoryResponseDto> checkedCategory = categoryService.getCategoriesByScrapId(scrapId);

        List<String> checkedCategoryNames = checkedCategory.stream()
            .map(CategoryResponseDto::getCategoryName)
            .toList();

        model.addAttribute("scrappedArticle", detailDto);
        model.addAttribute("relatedStocks", relatedStocks);
        model.addAttribute("categories", categories);
        model.addAttribute("checkedCategories", checkedCategoryNames);

        return "layout/mypage/detail";
    }
}



