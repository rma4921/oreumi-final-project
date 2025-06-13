package com.estsoft.finalproject.Post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.Post.dto.ScrapPostResponseDto;
import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ScrapPostServiceTest {

    @InjectMocks
    private ScrapPostService scrapPostService;

    @Mock
    private ScrapPostRepository scrapPostRepository;

    @Mock
    private ScrappedArticleRepository scrappedArticleRepository;

    private ScrappedArticle article;

    private Users tester;

    @BeforeEach
    public void setUp() {
        tester = new Users("네이버", "tester@naver.com", "tester", Role.ROLE_USER);
        tester.updateId(1L);

        article = ScrappedArticle.builder()
            .scrapId(1L)
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("테스트 중입니다.")
            .link("https://www.google.com")
            .description("테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test")
            .build();
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    void savePost() {
        Long scrapId = article.getScrapId();

        when(scrapPostRepository.existsByScrappedArticle_ScrapId(scrapId))
            .thenReturn(false);
        when(scrappedArticleRepository.findById(scrapId))
            .thenReturn(Optional.of(article));

        scrapPostService.savePost(scrapId, tester);

        verify(scrappedArticleRepository, times(1)).findById(scrapId);
        verify(scrapPostRepository, times(1))
            .existsByScrappedArticle_ScrapId(scrapId);
        verify(scrapPostRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 게시글 저장 시 테스트")
    void savePostAlreadyExist() {
        Long scrapId = article.getScrapId();

        when(scrapPostRepository.existsByScrappedArticle_ScrapId(scrapId))
            .thenReturn(true);

        assertThatThrownBy(() -> scrapPostService.savePost(scrapId, tester))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("이미 게시된 스크랩 기사입니다.");

        verify(scrappedArticleRepository, times(0)).findById(scrapId);
        verify(scrapPostRepository, times(1))
            .existsByScrappedArticle_ScrapId(scrapId);
        verify(scrappedArticleRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 스크랩 ID 저장 시 테스트")
    void savePostNotExistScrapId() {
        Long scrapId = 1000L;

        when(scrapPostRepository.existsByScrappedArticle_ScrapId(scrapId))
            .thenReturn(false);

        assertThatThrownBy(() -> scrapPostService.savePost(scrapId, tester))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당 스크랩 기사가 존재하지 않습니다.");

        verify(scrappedArticleRepository, times(1)).findById(scrapId);
        verify(scrapPostRepository, times(1))
            .existsByScrappedArticle_ScrapId(scrapId);
        verify(scrappedArticleRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("본인 소유 아닌 스크랩 기사 공유 시 테스트")
    void savePostNotExistUser() {
        Long scrapId = article.getScrapId();
        Users tester2 = new Users("네이버", "tester@naver.com", "tester", Role.ROLE_USER);
        tester2.updateId(2L);

        when(scrapPostRepository.existsByScrappedArticle_ScrapId(scrapId))
            .thenReturn(false);
        when(scrappedArticleRepository.findById(scrapId))
            .thenReturn(Optional.of(article));

        assertThatThrownBy(() -> scrapPostService.savePost(scrapId, tester2))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("본인의 스크랩 기사만 공유할 수 있습니다.");

        verify(scrappedArticleRepository, times(1)).findById(scrapId);
        verify(scrapPostRepository, times(1))
            .existsByScrappedArticle_ScrapId(scrapId);
        verify(scrappedArticleRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("전체 게시글 조회 테스트")
    void getAllPosts() {
        ScrapPost scrapPost = ScrapPost.builder()
            .postId(1L)
            .scrappedArticle(article)
            .postDate(LocalDateTime.now())
            .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ScrapPost> page = new PageImpl<>(List.of(scrapPost));

        when(scrapPostRepository.findAll(pageable))
            .thenReturn(page);

        Page<ScrapPostResponseDto> result = scrapPostService
            .getAllPosts(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getPostId()).isEqualTo(article.getScrapId());
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 중입니다.");

        verify(scrapPostRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("전체 게시글 검색 테스트")
    void getAllPostsByTitle() {
        ScrapPost scrapPost = ScrapPost.builder()
            .postId(1L)
            .scrappedArticle(article)
            .postDate(LocalDateTime.now())
            .build();
        String keyword = "테스트";

        Pageable pageable = PageRequest.of(0, 10);
        Page<ScrapPost> page = new PageImpl<>(List.of(scrapPost));

        when(scrapPostRepository.findByScrappedArticle_TitleContaining(keyword, pageable))
            .thenReturn(page);

        Page<ScrapPostResponseDto> result = scrapPostService
            .getAllPostsByTitle(keyword, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getPostId()).isEqualTo(article.getScrapId());
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 중입니다.");

        verify(scrapPostRepository, times(1))
            .findByScrappedArticle_TitleContaining(keyword, pageable);

    }

}