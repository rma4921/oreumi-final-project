package com.estsoft.finalproject.Post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.Post.dto.ScrapPostResponseDto;
import com.estsoft.finalproject.Post.service.ScrapPostService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;


@ExtendWith(MockitoExtension.class)
class ScrapPostControllerTest {
    @InjectMocks
    private ScrapPostController scrapPostController;

    @Mock
    private ScrapPostService scrapPostService;

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ConcurrentModel();
    }

    @Test
    @DisplayName("스크랩한 기사 저장")
    void savePost() throws Exception {
        Long scrapId = 1L;
        Users user = new Users("네이버", "tester@naver.com", "tester", Role.ROLE_USER);

        CustomUsersDetails userDetail = new CustomUsersDetails(user);

        String result = scrapPostController.saveScrapPost(scrapId, userDetail);

        assertThat(result).isEqualTo("redirect:/mypage/scrap/" + scrapId);
        verify(scrapPostService, times(1)).savePost(scrapId, user);
    }

    @Test
    @DisplayName("소통 게시판 게시글 호출 테스트")
    void getScrapPosts() throws Exception {
        ScrapPostResponseDto scrapPost = ScrapPostResponseDto.builder()
            .postId(1L)
            .nickname("tester")
            .title("테스트를 위한 제목")
            .topic("테스트")
            .postDate(LocalDateTime.now())
            .build();

        Pageable pageable = PageRequest.of(0, 10,
            Sort.by(Direction.DESC, "postDate"));
        Page<ScrapPostResponseDto> page = new PageImpl<>(List.of(scrapPost), pageable, 1);

        when(scrapPostService.getAllPosts(any()))
            .thenReturn(page);

        String viewName = scrapPostController.getScrapPosts(0, null, model);

        assertThat(viewName).isEqualTo("layout/post/post");
        assertThat(model.getAttribute("posts")).isEqualTo(page.getContent());
        assertThat(model.getAttribute("currentPage")).isEqualTo(0);
        assertThat(model.getAttribute("totalPages")).isEqualTo(1);
        assertThat(model.getAttribute("startPage")).isEqualTo(0);
        assertThat(model.getAttribute("endPage")).isEqualTo(0);
        assertThat(model.getAttribute("hasPrevBlock")).isEqualTo(false);
        assertThat(model.getAttribute("hasNextBlock")).isEqualTo(false);
        assertThat(model.getAttribute("keyword")).isEqualTo(null);
    }

    @Test
    @DisplayName("소통 게시판 게시글 검색 테스트")
    void getScrapPosts_withKeyword() throws Exception {
        ScrapPostResponseDto scrapPost = ScrapPostResponseDto.builder()
            .postId(1L)
            .nickname("tester")
            .title("테스트를 위한 제목")
            .topic("테스트")
            .postDate(LocalDateTime.now())
            .build();
        String keyword = "제목";

        Pageable pageable = PageRequest.of(0, 10,
            Sort.by(Direction.DESC, "postDate"));
        Page<ScrapPostResponseDto> page = new PageImpl<>(List.of(scrapPost), pageable, 1);

        when(scrapPostService.getAllPostsByTitle(keyword, pageable))
            .thenReturn(page);

        String viewName = scrapPostController.getScrapPosts(0, keyword, model);

        assertThat(viewName).isEqualTo("layout/post/post");
        assertThat(model.getAttribute("posts")).isEqualTo(page.getContent());
        assertThat(model.getAttribute("currentPage")).isEqualTo(0);
        assertThat(model.getAttribute("totalPages")).isEqualTo(1);
        assertThat(model.getAttribute("startPage")).isEqualTo(0);
        assertThat(model.getAttribute("endPage")).isEqualTo(0);
        assertThat(model.getAttribute("hasPrevBlock")).isEqualTo(false);
        assertThat(model.getAttribute("hasNextBlock")).isEqualTo(false);
        assertThat(model.getAttribute("keyword")).isEqualTo(keyword);
    }

}