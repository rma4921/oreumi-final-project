package com.estsoft.finalproject.mypage.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.estsoft.finalproject.mypage.service.ScrappedArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ScrappedArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScrappedArticleService scrappedArticleService;

    @Test
    @DisplayName("스크랩한 게시글 삭제 테스트")
    void deleteScrappedArticle() throws Exception {
        Long scrapId = 1L;
        doNothing().when(scrappedArticleService).deleteScrappedArticle(scrapId);

        ResultActions resultActions = mockMvc.perform(delete("/api/mypage/1"));

        resultActions.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/mypage"));

        verify(scrappedArticleService, times(1)).deleteScrappedArticle(scrapId);
    }
}