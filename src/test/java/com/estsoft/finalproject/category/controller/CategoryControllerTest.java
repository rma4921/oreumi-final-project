package com.estsoft.finalproject.category.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.estsoft.finalproject.category.service.CategoryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

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