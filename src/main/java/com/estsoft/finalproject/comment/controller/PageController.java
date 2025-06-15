package com.estsoft.finalproject.comment.controller;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
import com.estsoft.finalproject.category.dto.CategoryResponseDto;
import com.estsoft.finalproject.category.service.CategoryService;
import com.estsoft.finalproject.mypage.dto.RelatedStockResponseDTO;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleDetailResponseDto;
import com.estsoft.finalproject.mypage.service.RelatedStockService;
import com.estsoft.finalproject.mypage.service.ScrappedArticleService;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final ScrapPostRepository scrapPostRepository;
    private final CategoryService categoryService;
    private final ScrappedArticleService scrappedArticleService;
    private final RelatedStockService relatedStockService;

    @GetMapping("/posts/{postId}")
    public String showCommentPage(@PathVariable Long postId,
        Model model,
        @AuthenticationPrincipal CustomUsersDetails userDetails) {

        ScrapPost scrapPost = scrapPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("해당 공유 게시글이 없습니다."));

        ScrappedArticleDetailResponseDto detailDto = scrappedArticleService
            .getScrappedArticleDetail(scrapPost.getScrappedArticle().getScrapId());
        List<RelatedStockResponseDTO> relatedStocks = relatedStockService
            .findByScrapId(scrapPost.getScrappedArticle().getScrapId());
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        List<CategoryResponseDto> checkedCategory = categoryService
            .getCategoriesByScrapId(scrapPost.getScrappedArticle().getScrapId());

        List<String> checkedCategoryNames = checkedCategory.stream()
            .map(CategoryResponseDto::getCategoryName)
            .toList();

        if (userDetails != null) {
            model.addAttribute("user", userDetails.getUser());
            model.addAttribute("userId", userDetails.getUser().getUserId());
        }

        model.addAttribute("postId", postId);
        model.addAttribute("scrappedArticle", detailDto);
        model.addAttribute("relatedStocks", relatedStocks);
        model.addAttribute("categories", categories);
        model.addAttribute("checkedCategories", checkedCategoryNames);

        return "postDetail";
    }
}