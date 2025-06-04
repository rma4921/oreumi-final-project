package com.estsoft.finalproject.Post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.User;
import com.estsoft.finalproject.user.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ScrapPostServiceTest {

    @Autowired
    private ScrapPostService scrapPostService;

    @Autowired
    private ScrapPostRepository scrapPostRepository;

    @Autowired
    private ScrappedArticleRepository scrappedArticleRepository;

    @Autowired
    private UserRepository userRepository;

    private ScrappedArticle article;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        scrappedArticleRepository.deleteAll();
        scrapPostRepository.deleteAll();

        User tester = new User();
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
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    void savePost() {
        scrapPostService.savePost(article.getScrapId());

        assertThat(scrapPostRepository
            .existsByScrappedArticle_ScrapId(article.getScrapId())).isTrue();
        assertThatThrownBy(() -> scrapPostService.savePost(article.getScrapId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("이미 게시된 스크랩 기사입니다.");
        assertThatThrownBy(() -> scrapPostService.savePost(999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당 스크랩 기사가 존재하지 않습니다.");
    }
}