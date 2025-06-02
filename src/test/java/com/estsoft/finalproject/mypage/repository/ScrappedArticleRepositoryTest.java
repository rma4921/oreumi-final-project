package com.estsoft.finalproject.mypage.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;

import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.user.User;
import com.estsoft.finalproject.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ScrappedArticleRepositoryTest {

    @Autowired
    private ScrappedArticleRepository scrappedArticleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자가 스크랩한 기사 가져오기 테스트")
    void findAllByUserOrderByScrapDateDesc() {
        User tester = new User();
        tester.updateNickname("tester");
        User tester2 = new User();
        tester2.updateNickname("tester2");

        userRepository.save(tester);
        userRepository.save(tester2);

        ScrappedArticle article1 = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("첫 번째 테스트 중입니다.")
            .link("https://www.naver.com")
            .description("첫 번째 테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test1")
            .build();
        ScrappedArticle article2 = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("두 번째 테스트 중입니다.")
            .link("https://www.alan.com")
            .description("두 번째 테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test2")
            .build();
        ScrappedArticle article3 = ScrappedArticle.builder()
            .user(tester2)
            .scrapDate(LocalDateTime.now())
            .title("세 번째 테스트 중입니다.")
            .link("https://www.zum.com")
            .description("세 번째 테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test3")
            .build();

        scrappedArticleRepository.save(article1);
        scrappedArticleRepository.save(article2);
        scrappedArticleRepository.save(article3);

        Page<ScrappedArticle> result = scrappedArticleRepository
            .findAllByUserOrderByScrapDateDesc(tester, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTopic())
            .isEqualTo("test2");
        assertThat(result.getContent().get(1).getTopic())
            .isEqualTo("test1");
    }

    @Test
    @DisplayName("사용자가 스크랩한 기사 중 같은 제목만 가져오기 테스트")
    void findByUserAndTitleContainingOrderByScrapDateDesc() {
        User tester = new User();
        tester.updateNickname("tester");
        User tester2 = new User();
        tester2.updateNickname("tester2");

        userRepository.save(tester);
        userRepository.save(tester2);

        ScrappedArticle article1 = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("첫 번째 테스트 중입니다.")
            .link("https://www.zum.com")
            .description("첫 번째 테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test")
            .build();
        ScrappedArticle article2 = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("두 번째 테스트 중입니다.")
            .link("https://www.google.com")
            .description("두 번째 테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test2")
            .build();
        ScrappedArticle article3 = ScrappedArticle.builder()
            .user(tester2)
            .scrapDate(LocalDateTime.now())
            .title("세 번째 테스트 중입니다.")
            .link("https://www.alan.com")
            .description("세 번째 테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test3")
            .build();

        scrappedArticleRepository.save(article1);
        scrappedArticleRepository.save(article2);
        scrappedArticleRepository.save(article3);

        Page<ScrappedArticle> result = scrappedArticleRepository
            .findByUserAndTitleContainingOrderByScrapDateDesc(tester, "첫 번째",
                PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("첫 번째 테스트 중입니다.");
    }

    @Test
    @DisplayName("ScrapId로 스크랩한 기사 조회 테스트")
    void findById() {
        User tester = new User();
        tester.updateNickname("tester");
        userRepository.save(tester);

        ScrappedArticle article = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("아이디는 저장이 될까요?")
            .link("https://www.zum.com")
            .description("더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test")
            .build();
        scrappedArticleRepository.save(article);

        ScrappedArticle result = scrappedArticleRepository.findById(article.getScrapId())
            .orElseThrow();

        assertThat(result.getUser().getId()).isEqualTo(tester.getId());
        assertThat(result.getTitle()).isEqualTo("아이디는 저장이 될까요?");
        assertThat(result.getLink()).isEqualTo("https://www.zum.com");
        assertThat(result.getDescription()).isEqualTo("더미데이터입니다.");
        assertThat(result.getTopic()).isEqualTo("test");
    }

    @Test
    @DisplayName("스크랩한 기사 삭제 테스트")
    void deleteById() {
        User tester = new User();
        tester.updateNickname("tester");
        userRepository.save(tester);

        ScrappedArticle article = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("테스트 중입니다.")
            .link("https://www.zum.com")
            .description("더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test")
            .build();

        scrappedArticleRepository.save(article);
        scrappedArticleRepository.deleteById(article.getScrapId());

        Optional<ScrappedArticle> result = scrappedArticleRepository.findById(article.getScrapId());

        assertThat(result).isEmpty();
    }
}