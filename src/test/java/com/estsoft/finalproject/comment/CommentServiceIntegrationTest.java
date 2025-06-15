package com.estsoft.finalproject.comment;

import com.estsoft.finalproject.comment.dto.CommentRequest;
import com.estsoft.finalproject.comment.entity.Comment;
import com.estsoft.finalproject.comment.entity.User;
import com.estsoft.finalproject.comment.repository.CommentRepository;
import com.estsoft.finalproject.comment.repository.UserRepository;
import com.estsoft.finalproject.comment.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;


    @Autowired
    private UserRepository userRepository;

    @Test
    void total_comments_test() {
        // given
        User user = userRepository.save(User.builder().nickname("통합테스터").build());

        Comment comment = commentService.save(
            CommentRequest.builder()
                .postId(1L)
                .userId(user.getId())
                .content("통합 테스트용 댓글")
                .build()
        );

        // then
        assertNotNull(comment.getId());
        assertEquals("통합 테스트용 댓글", comment.getContent());
    }
}