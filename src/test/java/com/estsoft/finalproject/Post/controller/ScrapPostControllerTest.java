package com.estsoft.finalproject.Post.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import com.estsoft.finalproject.Post.service.ScrapPostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ScrapPostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScrapPostService scrapPostService;

    @Test
    @DisplayName("스크랩한 기사 저장")
    void savePost() throws Exception {
        Long scrapId = 1L;

        mockMvc.perform(post("/api/mypage/{scrapId}", scrapId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/mypage/scrap/" + scrapId));

        verify(scrapPostService, times(1)).savePost(scrapId);
    }
}