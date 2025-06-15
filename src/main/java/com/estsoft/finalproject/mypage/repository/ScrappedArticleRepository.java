package com.estsoft.finalproject.mypage.repository;

import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrappedArticleRepository extends JpaRepository<ScrappedArticle, Long> {

    Page<ScrappedArticle> findAllByUserOrderByScrapDateDesc(User user, Pageable pageable);

    Page<ScrappedArticle> findByUserAndTitleContainingOrderByScrapDateDesc(User user,
        String keyword, Pageable pageable);
}
