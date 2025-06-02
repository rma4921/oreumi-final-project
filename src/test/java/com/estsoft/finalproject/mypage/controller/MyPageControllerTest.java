package com.estsoft.finalproject.mypage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.estsoft.finalproject.mypage.dto.RelatedStockResponseDTO;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleDetailResponseDto;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
import com.estsoft.finalproject.mypage.service.RelatedStockService;
import com.estsoft.finalproject.mypage.service.ScrappedArticleService;
import com.estsoft.finalproject.security.UserDetail;
import com.estsoft.finalproject.user.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class MyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScrappedArticleService scrappedArticleService;

    @MockitoBean
    private RelatedStockService relatedStockService;

    // 동엽님 코드에 따라 수정해야 함.
    @BeforeEach
    public void setUpUser() {
        User mockUser = new User();
        mockUser.updateId(1L);
        mockUser.updateNickname("테스트유저1");

        UserDetail userDetail = new UserDetail(mockUser);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("메인 페이지 기본 화면 테스트")
    public void getScrappedArticles_NoKeyWord() throws Exception {
        List<ScrappedArticleResponseDto> article = List.of(
            new ScrappedArticleResponseDto(1L, "Test 중입니다.", "test", LocalDateTime.now())
        );
        Page<ScrappedArticleResponseDto> page = new PageImpl<>(article, PageRequest.of(0, 10), 1);

        given(scrappedArticleService.getScrappedArticlesByUser(any(), any()))
            .willReturn(page);

        ResultActions resultActions = mockMvc.perform(get("/mypage")
            .param("scrapPage", "0")
            .param("keyword", "")
        );

        resultActions.andExpect(status().isOk())
            .andExpect(view().name("layout/mypage/mypage"))
            .andExpect(model().attributeExists(
                "scrappedArticle", "totalPages", "currentPage",
                "startPage", "endPage", "hasPrevBlock", "hasNextBlock", "keyword"
            ));
    }

    @Test
    @DisplayName("메인 페이지 검색 화면 테스트")
    public void getScrappedArticles_WithKeyWord() throws Exception {
        List<ScrappedArticleResponseDto> articles = List.of(
            new ScrappedArticleResponseDto(1L, "Test 중입니다.", "test", LocalDateTime.now())
        );
        Page<ScrappedArticleResponseDto> page = new PageImpl<>(articles, PageRequest.of(0, 10), 1);

        given(scrappedArticleService.getScrappedArticlesByUserAndTitle(any(), any(), any()))
            .willReturn(page);

        ResultActions resultActions = mockMvc.perform(get("/mypage")
            .param("scrapPage", "0")
            .param("keyword", "tester")
        );

        resultActions.andExpect(status().isOk())
            .andExpect(view().name("layout/mypage/mypage"))
            .andExpect(model().attributeExists(
                "scrappedArticle", "totalPages", "currentPage",
                "startPage", "endPage", "hasPrevBlock", "hasNextBlock", "keyword"
            ));
    }

    @Test
    @DisplayName("게시글 상세 보기 테스트")
    public void getScrappedArticleDetail() throws Exception {
        ScrappedArticleDetailResponseDto article =
            new ScrappedArticleDetailResponseDto(
                1L, "Test 중입니다.", "test", LocalDateTime.now(),
                "요약", "https://www.naver.com", LocalDateTime.now(),
                false
            );
        List<RelatedStockResponseDTO> stocks = List.of(
            new RelatedStockResponseDTO("테스트1"),
            new RelatedStockResponseDTO("테스트2"),
            new RelatedStockResponseDTO("테스트3")
        );

        given(scrappedArticleService.getScrappedArticleDetail(1L))
            .willReturn(article);
        given(relatedStockService.findByScrapId(1L))
            .willReturn(stocks);

        ResultActions resultActions = mockMvc.perform(get("/mypage/scrap/1"));

        resultActions.andExpect(status().isOk())
            .andExpect(view().name("layout/mypage/detail"))
            .andExpect(model().attributeExists("scrappedArticle", "relatedStocks"));
    }
}