package com.estsoft.finalproject.mypage.repository;

import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.user.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrappedArticleRepository extends JpaRepository<ScrappedArticle, Long> {

    Page<ScrappedArticle> findAllByUserOrderByScrapDateDesc(Users user, Pageable pageable);

    Page<ScrappedArticle> findByUserAndTitleContainingOrderByScrapDateDesc(Users user,
        String keyword, Pageable pageable);
}
