package com.estsoft.finalproject.mypage.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleDetailResponseDto;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.User;
import com.estsoft.finalproject.user.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ScrappedArticleServiceTest {

    @Autowired
    private ScrappedArticleRepository scrappedArticleRepository;

    @Autowired
    private ScrappedArticleService scrappedArticleService;

    @Autowired
    private UserRepository userRepository;

    private User tester;

    private ScrappedArticle article;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        scrappedArticleRepository.deleteAll();

        tester = new User();
        tester.updateNickname("tester");
        userRepository.save(tester);

        for (int i = 1; i <= 3; i++ ) {
            article = scrappedArticleRepository.save(ScrappedArticle.builder()
                .user(tester)
                .scrapDate(LocalDateTime.now())
                .title(i + "번째 테스트 중입니다.")
                .link("https://www.naver.com")
                .description(i + "번째 테스트용 더미데이터입니다.")
                .pubDate(LocalDateTime.now())
                .topic("test" + i)
                .build()
            );
        }
    }

    @Test
    @DisplayName("스크랩한 기사 조회 테스트")
    void getScrappedArticlesByUser() {
        Page<ScrappedArticleResponseDto> result = scrappedArticleService
            .getScrappedArticlesByUser(tester, PageRequest
                .of(0, 10, Sort.by(Direction.DESC, "scrapDate")));

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTopic()).isEqualTo("test1");
        assertThat(result.getContent().get(1).getTopic()).isEqualTo("test2");
        assertThat(result.getContent().get(2).getTopic()).isEqualTo("test3");
    }

    @Test
    @DisplayName("스크랩한 기사 제목 조회 테스트")
    void getScrappedArticlesByUserAndTitle() {
        Page<ScrappedArticleResponseDto> result = scrappedArticleService
            .getScrappedArticlesByUserAndTitle(tester, "2번째", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTopic()).isEqualTo("test2");
    }

    @Test
    @DisplayName("스크랩한 기사 상세보기 테스트")
    void getScrappedArticleDetail() {
        ScrappedArticleDetailResponseDto result = scrappedArticleService
            .getScrappedArticleDetail(article.getScrapId());

        assertThat(result.getScrapId()).isEqualTo(article.getScrapId());
        assertThat(result.getTitle()).isEqualTo(article.getTitle());
    }

    @Test
    @DisplayName("스크랩한 기사 삭제 테스트")
    void deleteScrappedArticle() {
        scrappedArticleService.deleteScrappedArticle(article.getScrapId());

        assertThat(scrappedArticleRepository.findById(article.getScrapId())).isEmpty();
    }
}