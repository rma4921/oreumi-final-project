package com.estsoft.finalproject.mypage.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.estsoft.finalproject.category.dto.CategoryResponseDto;
import com.estsoft.finalproject.category.service.CategoryService;
import com.estsoft.finalproject.comment.dto.CommentResponseDto;
import com.estsoft.finalproject.comment.service.CommentService;
import com.estsoft.finalproject.mypage.dto.RelatedStockResponseDTO;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleDetailResponseDto;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
import com.estsoft.finalproject.mypage.service.RelatedStockService;
import com.estsoft.finalproject.mypage.service.ScrappedArticleService;
import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class MyPageControllerTest {

    @InjectMocks
    private MyPageController myPageController;

    @Mock
    private ScrappedArticleService scrappedArticleService;

    @Mock
    private RelatedStockService relatedStockService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CommentService commentService;

    private Model model;

    private CustomUsersDetails userDetail;

    @BeforeEach
    public void setUpUser() {
        model = new ConcurrentModel();
        Users mockUser = new Users("네이버", "tester@naver.com", "tester", Role.ROLE_USER);

        userDetail = new CustomUsersDetails(mockUser);
    }

    @Test
    @DisplayName("메인 페이지 기본 화면 테스트 - 스크랩 탭")
    public void getScrappedArticles_NoKeyWord_Scrap() throws Exception {
        Long scrapId = 1L;

        List<ScrappedArticleResponseDto> article = List.of(
            new ScrappedArticleResponseDto(scrapId, "Test 중입니다.", "test", LocalDateTime.now())
        );
        Page<ScrappedArticleResponseDto> page = new PageImpl<>(article, PageRequest.of(0, 10), 1);

        given(scrappedArticleService.getScrappedArticlesByUser(any(), any()))
            .willReturn(page);
        given(categoryService.getCategoriesByScrapId(scrapId))
            .willReturn(List.of(new CategoryResponseDto("경제"), new CategoryResponseDto("사회")));

        String viewName = myPageController.getScrappedArticles(userDetail, 0, "scrap", 0, null,
            model);

        assertThat(viewName).isEqualTo("layout/mypage/mypage");
        assertThat(model.containsAttribute("scrappedArticle")).isTrue();
        assertThat(model.containsAttribute("articlesCategory")).isTrue();
        assertThat(model.getAttribute("currentPage")).isEqualTo(0);
        assertThat(model.getAttribute("totalPages")).isEqualTo(1);
        assertThat(model.getAttribute("tab")).isEqualTo("scrap");
    }

    @Test
    @DisplayName("메인 페이지 검색 화면 테스트 - 스크랩 탭")
    public void getScrappedArticles_WithKeyWord_Scrap() throws Exception {
        Long scrapId = 1L;
        List<ScrappedArticleResponseDto> articles = List.of(
            new ScrappedArticleResponseDto(scrapId, "Test 중입니다.", "test", LocalDateTime.now())
        );
        Page<ScrappedArticleResponseDto> page = new PageImpl<>(articles, PageRequest.of(0, 10), 1);

        given(scrappedArticleService.getScrappedArticlesByUserAndTitle(any(), any(), any()))
            .willReturn(page);
        given(categoryService.getCategoriesByScrapId(scrapId))
            .willReturn(List.of(new CategoryResponseDto("경제"), new CategoryResponseDto("사회")));

        String viewName = myPageController.getScrappedArticles(userDetail, 0, "scrap", 0, "Test",
            model);

        assertThat(viewName).isEqualTo("layout/mypage/mypage");
        assertThat(model.containsAttribute("scrappedArticle")).isTrue();
        assertThat(model.containsAttribute("articlesCategory")).isTrue();
        assertThat(model.getAttribute("currentPage")).isEqualTo(0);
        assertThat(model.getAttribute("totalPages")).isEqualTo(1);
        assertThat(model.getAttribute("keyword")).isEqualTo("Test");
        assertThat(model.getAttribute("tab")).isEqualTo("scrap");
    }

    @Test
    @DisplayName("메인 페이지 기본 화면 테스트 - 댓글 탭")
    public void getScrappedArticles_NoKeyWord_Comment() throws Exception {
        Long scrapId = 1L;

        List<CommentResponseDto> comment = List.of(
            new CommentResponseDto(1L, scrapId, 1L, "제목입니다.", "댓글입니다.", LocalDateTime.now())
        );
        Page<CommentResponseDto> page = new PageImpl<>(comment, PageRequest.of(0, 10), 1);

        given(commentService.getCommentsByUser(any(), any()))
            .willReturn(page);

        String viewName = myPageController.getScrappedArticles(userDetail, 0, "comment", 0, null,
            model);

        assertThat(viewName).isEqualTo("layout/mypage/mypage");
        assertThat(model.containsAttribute("comments")).isTrue();
        assertThat(model.getAttribute("currentPage")).isEqualTo(0);
        assertThat(model.getAttribute("totalPages")).isEqualTo(1);
        assertThat(model.getAttribute("tab")).isEqualTo("comment");
    }

    @Test
    @DisplayName("메인 페이지 검색 화면 테스트 - 댓글 탭")
    public void getScrappedArticles_WithKeyWord_Comment() throws Exception {
        Long scrapId = 1L;
        Long postId = 1L;

        List<CommentResponseDto> comment = List.of(
            new CommentResponseDto(1L, scrapId, postId, "제목입니다.", "댓글입니다.", LocalDateTime.now())
        );
        Page<CommentResponseDto> page = new PageImpl<>(comment, PageRequest.of(0, 10), 1);

        given(commentService.findByUserAndContentContaining(any(), any(), any()))
            .willReturn(page);

        String viewName = myPageController.getScrappedArticles(userDetail, 0, "comment", 0, "댓글",
            model);

        assertThat(viewName).isEqualTo("layout/mypage/mypage");
        assertThat(model.containsAttribute("comments")).isTrue();
        assertThat(model.getAttribute("currentPage")).isEqualTo(0);
        assertThat(model.getAttribute("totalPages")).isEqualTo(1);
        assertThat(model.getAttribute("keyword")).isEqualTo("댓글");
        assertThat(model.getAttribute("tab")).isEqualTo("comment");
    }

    @Test
    @DisplayName("게시글 상세 보기 테스트")
    public void getScrappedArticleDetail() throws Exception {
        Long scrapId = 1L;
        Long postId = 1L;

        ScrappedArticleDetailResponseDto article =
            new ScrappedArticleDetailResponseDto(
                scrapId, postId, "Test 중입니다.", "test", LocalDateTime.now(),
                "요약", "https://www.naver.com", LocalDateTime.now(),
                false
            );

        List<RelatedStockResponseDTO> stocks = List.of(
            new RelatedStockResponseDTO("테스트1"),
            new RelatedStockResponseDTO("테스트2"),
            new RelatedStockResponseDTO("테스트3")
        );

        List<CategoryResponseDto> allCategories = List.of(new CategoryResponseDto("정치"),
            new CategoryResponseDto("경제"),
            new CategoryResponseDto("사회"),
            new CategoryResponseDto("생활/문화"),
            new CategoryResponseDto("IT/과학"),
            new CategoryResponseDto("세계")
        );

        List<CategoryResponseDto> checkedCategories = List.of(
            new CategoryResponseDto("경제"), new CategoryResponseDto("사회")
        );

        List<String> checkedCategoryNames = checkedCategories.stream()
            .map(CategoryResponseDto::getCategoryName)
            .toList();

        given(scrappedArticleService.getScrappedArticleDetail(scrapId))
            .willReturn(article);
        given(relatedStockService.findByScrapId(scrapId))
            .willReturn(stocks);
        given(categoryService.getAllCategories())
            .willReturn(allCategories);
        given(categoryService.getCategoriesByScrapId(scrapId))
            .willReturn(checkedCategories);

        String viewName = myPageController.getScrappedArticleDetail(scrapId, model);

        assertThat(viewName).isEqualTo("layout/mypage/detail");
        assertThat(model.getAttribute("scrappedArticle")).isEqualTo(article);
        assertThat(model.getAttribute("relatedStocks")).isEqualTo(stocks);
        assertThat(model.getAttribute("categories")).isEqualTo(allCategories);
        assertThat(model.getAttribute("checkedCategories")).isEqualTo(checkedCategoryNames);
    }
}