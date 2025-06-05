package com.estsoft.finalproject.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.estsoft.finalproject.category.domain.Category;
import com.estsoft.finalproject.category.domain.ScrappedArticleCategory;
import com.estsoft.finalproject.category.dto.CategoryResponseDto;
import com.estsoft.finalproject.category.repository.CategoryRepository;
import com.estsoft.finalproject.category.repository.ScrappedArticleCategoryRepository;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.User;
import com.estsoft.finalproject.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ScrappedArticleCategoryRepository scrappedArticleCategoryRepository;

    @Autowired
    private ScrappedArticleRepository scrappedArticleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User tester;

    private ScrappedArticle article;

    private Category category;

    @BeforeEach
    void setUp() {
        scrappedArticleCategoryRepository.deleteAll();
        scrappedArticleRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        tester = new User();
        tester.updateNickname("tester");
        userRepository.save(tester);

        article = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("테스트 중입니다.")
            .link("https://www.google.com")
            .description("테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test")
            .build();
        scrappedArticleRepository.save(article);

        category = new Category("경제");
        categoryRepository.save(category);

        ScrappedArticleCategory sac = ScrappedArticleCategory.builder()
            .scrappedArticle(article)
            .category(category)
            .build();
        scrappedArticleCategoryRepository.save(sac);
    }

    @Test
    @DisplayName("카테고리 업데이트 테스트")
    void updateCategories() {
        Category updateCategory = new Category("사회");
        categoryRepository.save(updateCategory);

        categoryService.updateCategories(article.getScrapId(), List.of("사회"));

        List<CategoryResponseDto> result = categoryService.getCategoriesByScrapId(
            article.getScrapId());

        assertEquals(1, result.size());
        assertThat(result.get(0).getCategoryName()).isEqualTo("사회");
    }

    @Test
    void getAllCategories() {
        Category updateCategory = new Category("사회");
        categoryRepository.save(updateCategory);

        List<CategoryResponseDto> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertThat(result).extracting(CategoryResponseDto::getCategoryName)
            .containsExactlyInAnyOrder("경제", "사회");
    }

    @Test
    void getCategoriesByScrapId() {
        Category category2 = new Category("IT/과학");
        categoryRepository.save(category2);

        ScrappedArticleCategory sac = ScrappedArticleCategory.builder()
            .scrappedArticle(article)
            .category(category2)
            .build();
        scrappedArticleCategoryRepository.save(sac);

        List<CategoryResponseDto> result = categoryService.getCategoriesByScrapId(article.getScrapId());

        assertEquals(2, result.size());
        assertThat(result).extracting(CategoryResponseDto::getCategoryName)
            .containsExactlyInAnyOrder("경제", "IT/과학");
    }
}