package com.estsoft.finalproject.Post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.estsoft.finalproject.Post.service.ScrapPostService;
import com.estsoft.finalproject.security.UserDetail;
import com.estsoft.finalproject.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ScrapPostControllerTest {
    @InjectMocks
    private ScrapPostController scrapPostController;

    @Mock
    private ScrapPostService scrapPostService;

    @Test
    @DisplayName("스크랩한 기사 저장")
    void savePost() throws Exception {
        Long scrapId = 1L;
        User user = new User();
        user.updateId(1L);
        UserDetail userDetail = new UserDetail(user);

        String result = scrapPostController.saveScrapPost(scrapId, userDetail);

        assertThat(result).isEqualTo("redirect:/mypage/scrap/" + scrapId);
        verify(scrapPostService, times(1)).savePost(scrapId, user);
    }
}