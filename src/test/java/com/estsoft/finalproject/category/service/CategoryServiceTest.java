package com.estsoft.finalproject.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.estsoft.finalproject.category.domain.Category;
import com.estsoft.finalproject.category.domain.ScrappedArticleCategory;
import com.estsoft.finalproject.category.dto.CategoryResponseDto;
import com.estsoft.finalproject.category.repository.CategoryRepository;
import com.estsoft.finalproject.category.repository.ScrappedArticleCategoryRepository;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.Users;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private ScrappedArticleCategoryRepository scrappedArticleCategoryRepository;

    @Mock
    private ScrappedArticleRepository scrappedArticleRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Users tester;

    private ScrappedArticle article;

    private Category category;

    @BeforeEach
    void setUp() {
        tester = new Users("네이버", "tester@naver.com", "tester", Role.ROLE_USER);

        article = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("테스트 중입니다.")
            .link("https://www.google.com")
            .description("테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test")
            .build();
        article.updateScrapId(1L);

        category = new Category("경제");
        category.updateCategoryId(1L);
    }

    @Test
    @DisplayName("카테고리 업데이트 테스트")
    void updateCategories() {
        Category updateCategory = new Category("사회");
        updateCategory.updateCategoryId(2L);

        when(scrappedArticleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(scrappedArticleCategoryRepository.findByScrappedArticle(article))
            .thenReturn(List.of(
                ScrappedArticleCategory.builder()
                    .scrappedArticle(article)
                    .category(category)
                    .build()
            ));
        when(categoryRepository.findByCategoryName("사회"))
            .thenReturn(Optional.of(updateCategory));

        categoryService.updateCategories(article.getScrapId(), List.of("사회"));

        verify(scrappedArticleCategoryRepository, times(1)).delete(any());
        verify(scrappedArticleCategoryRepository, times(1)).save(any());
    }

    @Test
    void getAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(
            new Category("경제"),
            new Category("사회")
        ));

        List<CategoryResponseDto> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2)
            .extracting(CategoryResponseDto::getCategoryName)
            .containsExactlyInAnyOrder("경제", "사회");
    }

    @Test
    void getCategoriesByScrapId() {
        when(scrappedArticleCategoryRepository.findCategoriesByScrapId(article.getScrapId()))
            .thenReturn(List.of(
                new Category("경제"),
                new Category("IT/과학")
            ));

        List<CategoryResponseDto> result = categoryService.getCategoriesByScrapId(
            article.getScrapId());

        assertEquals(2, result.size());
        assertThat(result).extracting(CategoryResponseDto::getCategoryName)
            .containsExactlyInAnyOrder("경제", "IT/과학");
    }
}