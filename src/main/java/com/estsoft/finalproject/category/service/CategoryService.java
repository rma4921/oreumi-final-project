package com.estsoft.finalproject.category.service;

import com.estsoft.finalproject.category.dto.CategoryResponseDto;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.category.domain.ScrappedArticleCategory;
import com.estsoft.finalproject.category.domain.Category;
import com.estsoft.finalproject.category.repository.ScrappedArticleCategoryRepository;
import com.estsoft.finalproject.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ScrappedArticleCategoryRepository scrappedArticleCategoryRepository;
    private final ScrappedArticleRepository scrappedArticleRepository;

    @Transactional
    public void updateCategories(Long scrapId, List<String> categories) {
        ScrappedArticle article = scrappedArticleRepository.findById(scrapId)
            .orElseThrow(() -> new IllegalArgumentException("해당하는 스크랩 기사가 없습니다."));

        List<ScrappedArticleCategory> currentCategories = scrappedArticleCategoryRepository
            .findByScrappedArticle(article);
        List<String> currentNames = currentCategories.stream()
            .map(category -> category.getCategory().getCategoryName())
            .toList();

        for (ScrappedArticleCategory scrappedArticleCategory : currentCategories) {
            if (!categories.contains(scrappedArticleCategory.getCategory().getCategoryName())) {
                scrappedArticleCategoryRepository.delete(scrappedArticleCategory);
            }
        }

        for (String categoryName : categories) {
            if (!currentNames.contains(categoryName)) {
                Category category = categoryRepository.findByCategoryName(categoryName)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 태그가 없습니다."));
                scrappedArticleCategoryRepository.save(ScrappedArticleCategory.builder()
                    .scrappedArticle(article)
                    .category(category)
                    .build()
                );
            }
        }
    }

    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
            .map(category -> CategoryResponseDto.builder()
                .categoryName(category.getCategoryName())
                .build()
            ).toList();
    }

    public List<CategoryResponseDto> getCategoriesByScrapId(Long scrapId) {
        List<Category> categories = scrappedArticleCategoryRepository.findCategoriesByScrapId(
            scrapId);

        return categories.stream()
            .map(category -> CategoryResponseDto.builder()
                .categoryName(category.getCategoryName())
                .build()
            ).toList();
    }
}
