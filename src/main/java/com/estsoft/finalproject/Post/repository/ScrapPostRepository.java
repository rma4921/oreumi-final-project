package com.estsoft.finalproject.Post.repository;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapPostRepository extends JpaRepository<ScrapPost, Long> {

    boolean existsByScrappedArticle_ScrapId(Long scrapId);
}
