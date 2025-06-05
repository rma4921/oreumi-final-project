package com.estsoft.finalproject.category.repository;

import com.estsoft.finalproject.category.domain.Category;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.category.domain.ScrappedArticleCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScrappedArticleCategoryRepository extends JpaRepository<ScrappedArticleCategory, Long> {

    void deleteByScrappedArticle(ScrappedArticle scrappedArticle);

    @Query("""
        SELECT sac.category
        FROM ScrappedArticleCategory sac
        WHERE sac.scrappedArticle.scrapId = :scrapId
        """)
    List<Category> findCategoriesByScrapId(@Param("scrapId") Long scrapId);

    List<ScrappedArticleCategory> findByScrappedArticle(ScrappedArticle article);
}
