package com.estsoft.finalproject.category.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.estsoft.finalproject.category.service.CategoryService;
import java.util.List;
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
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("카테고리 업데이트 테스트")
    void updateTags() throws Exception {
        Long scrapId = 1L;
        List<String> categories = List.of("정치", "경제", "사회");

        ResultActions resultActions = mockMvc.perform(
            post("/api/mypage/{scrapId}/categories", scrapId)
                .param("categoryNames", "정치", "경제", "사회"));

        resultActions.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/mypage/scrap/" + scrapId));
        verify(categoryService, times(1)).updateCategories(scrapId, categories);
    }

    @Test
    @DisplayName("카테고리 업데이트 테스트")
    void updateTagsWithoutCategory() throws Exception {
        Long scrapId = 1L;

        ResultActions resultActions = mockMvc.perform(
            post("/api/mypage/{scrapId}/categories", scrapId));

        resultActions.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/mypage/scrap/" + scrapId));
        verify(categoryService, times(1)).updateCategories(scrapId, List.of());
    }
}