package com.estsoft.finalproject.Post.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.repository.UsersRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ScrapPostRepositoryTest {

    @Autowired
    private ScrappedArticleRepository scrappedArticleRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private ScrapPostRepository scrapPostRepository;

    @Test
    @DisplayName("스크랩한 존재 유무 테스트")
    void existsByScrappedArticle_ScrapId() {
        Users tester = new Users();
        tester.updateNickname("tester");
        userRepository.save(tester);

        ScrappedArticle article = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("테스트 중입니다.")
            .link("https://www.google.com")
            .description("테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test")
            .build();
        scrappedArticleRepository.save(article);

        ScrapPost scrapPost = ScrapPost.builder()
            .scrappedArticle(article)
            .postDate(LocalDateTime.now())
            .build();
        scrapPostRepository.save(scrapPost);

        boolean isExist = scrapPostRepository.existsByScrappedArticle_ScrapId(article.getScrapId());
        boolean isExist2 = scrapPostRepository.existsByScrappedArticle_ScrapId(1234L);

        assertTrue(isExist);
        assertFalse(isExist2);
    }
}