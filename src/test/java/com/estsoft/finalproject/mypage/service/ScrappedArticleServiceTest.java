package com.estsoft.finalproject.mypage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleDetailResponseDto;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ScrappedArticleServiceTest {

    @Mock
    private ScrappedArticleRepository scrappedArticleRepository;

    @InjectMocks
    private ScrappedArticleService scrappedArticleService;

    @Mock
    private ScrapPostRepository scrapPostRepository;

    private Users tester;

    private ScrappedArticle article;

    @BeforeEach
    public void setUp() {
        tester = new Users();
        tester.updateNickname("tester");

        article = ScrappedArticle.builder()
            .scrapId(1L)
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("테스트 중입니다.")
            .link("https://www.naver.com")
            .description("테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test")
            .build();
    }

    @Test
    @DisplayName("스크랩한 기사 조회 테스트")
    void getScrappedArticlesByUser() {
        List<ScrappedArticle> articleList = List.of(article);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ScrappedArticle> page = new PageImpl<>(articleList);

        when(scrappedArticleRepository.findAllByUserOrderByScrapDateDesc(tester, pageable))
            .thenReturn(page);

        Page<ScrappedArticleResponseDto> result = scrappedArticleService
            .getScrappedArticlesByUser(tester, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTopic()).isEqualTo("test");
    }

    @Test
    @DisplayName("스크랩한 기사 제목 조회 테스트")
    void getScrappedArticlesByUserAndTitle() {
        List<ScrappedArticle> articleList = List.of(article);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ScrappedArticle> page = new PageImpl<>(articleList);

        when(scrappedArticleRepository
            .findByUserAndTitleContainingOrderByScrapDateDesc(tester, "테스트", pageable))
            .thenReturn(page);

        Page<ScrappedArticleResponseDto> result = scrappedArticleService
            .getScrappedArticlesByUserAndTitle(tester, "테스트", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTopic()).isEqualTo("test");
    }

    @Test
    @DisplayName("스크랩한 기사 상세보기 테스트")
    void getScrappedArticleDetail() {
        Long scrapId = article.getScrapId();

        when(scrappedArticleRepository.findById(scrapId))
            .thenReturn(Optional.of(article));
        when(scrapPostRepository.existsByScrappedArticle_ScrapId(scrapId))
            .thenReturn(true);

        ScrappedArticleDetailResponseDto result = scrappedArticleService
            .getScrappedArticleDetail(scrapId);

        assertThat(result.getScrapId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 중입니다.");
        assertThat(result.isShared()).isTrue();
    }

    @Test
    @DisplayName("스크랩한 기사 삭제 테스트")
    void deleteScrappedArticle() {
        scrappedArticleService.deleteScrappedArticle(article.getScrapId());

        verify(scrappedArticleRepository, times(1))
            .deleteById(article.getScrapId());
    }
}