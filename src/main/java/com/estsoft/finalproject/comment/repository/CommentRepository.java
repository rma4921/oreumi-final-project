package com.estsoft.finalproject.comment.repository;

import com.estsoft.finalproject.comment.domain.Comment;
import com.estsoft.finalproject.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByUser(User user, Pageable pageable);
    Page<Comment> findByUserAndContentContaining(User user, String keyword, Pageable pageable);
}
