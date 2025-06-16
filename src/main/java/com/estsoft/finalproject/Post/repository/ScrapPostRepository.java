package com.estsoft.finalproject.Post.repository;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapPostRepository extends JpaRepository<ScrapPost, Long> {

    boolean existsByScrappedArticle_ScrapId(Long scrapId);

    Page<ScrapPost> findByScrappedArticle_TitleContaining(String keyword, Pageable pageable);

    ScrapPost findByScrappedArticle(ScrappedArticle scrappedArticle);
}
