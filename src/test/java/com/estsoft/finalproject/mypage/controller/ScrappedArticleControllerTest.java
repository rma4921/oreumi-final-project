package com.estsoft.finalproject.mypage.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.estsoft.finalproject.mypage.service.ScrappedArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScrappedArticleControllerTest {

    @Mock
    private ScrappedArticleService scrappedArticleService;

    @InjectMocks
    private ScrappedArticleController scrappedArticleController;

    @Test
    @DisplayName("스크랩한 게시글 삭제 테스트")
    void deleteScrappedArticle() throws Exception {
        Long scrapId = 1L;
        doNothing().when(scrappedArticleService).deleteScrappedArticle(scrapId);

        String viewName = scrappedArticleController.deleteScrappedArticle(scrapId);

        assertThat(viewName).isEqualTo("redirect:/mypage");
        verify(scrappedArticleService, times(1)).deleteScrappedArticle(scrapId);
    }
}