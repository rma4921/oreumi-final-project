package com.estsoft.finalproject.comment.repository;

import com.estsoft.finalproject.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.estsoft.finalproject.user.domain.Users;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByUser_UserId(Long userId);
    Page<Comment> findByUser(Users user, Pageable pageable);
    Page<Comment> findByUserAndContentContaining(Users user, String keyword, Pageable pageable);
}
