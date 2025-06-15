package com.estsoft.finalproject.category.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.estsoft.finalproject.category.domain.Category;
import com.estsoft.finalproject.category.domain.ScrappedArticleCategory;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ScrappedArticleCategoryRepositoryTest {

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
    @DisplayName("스크랩 기사로 ScrapArticleCategory 삭제 테스트")
    void deleteByScrappedArticle() {
        scrappedArticleCategoryRepository.deleteByScrappedArticle(article);
        List<ScrappedArticleCategory> sacList = scrappedArticleCategoryRepository.findByScrappedArticle(
            article);

        assertThat(sacList).isEmpty();
    }

    @Test
    @DisplayName("ScrapId로 Category 조회 테스트")
    void findCategoriesByScrapId() {
        Category category2 = new Category("사회");
        categoryRepository.save(category2);

        ScrappedArticleCategory sac = ScrappedArticleCategory.builder()
            .scrappedArticle(article)
            .category(category2)
            .build();
        scrappedArticleCategoryRepository.save(sac);

        List<Category> categories = scrappedArticleCategoryRepository
            .findCategoriesByScrapId(article.getScrapId());
        List<String> categoryNames = categories.stream()
                .map(Category::getCategoryName)
                .toList();

        assertThat(categories).hasSize(2);
        assertThat(categoryNames).containsExactlyInAnyOrder("경제", "사회");
    }

    @Test
    @DisplayName("스크랩한 기사로 ScrappedArticleCategory 조회 테스트")
    void findByScrappedArticle() {
        List<ScrappedArticleCategory> result = scrappedArticleCategoryRepository.findByScrappedArticle(article);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory().getCategoryName()).isEqualTo("경제");
    }
}