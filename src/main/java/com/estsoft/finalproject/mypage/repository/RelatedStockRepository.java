package com.estsoft.finalproject.mypage.repository;

import com.estsoft.finalproject.mypage.domain.RelatedStock;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatedStockRepository extends JpaRepository<RelatedStock, Long> {

    List<RelatedStock> findByScrappedArticle_ScrapId(Long scrapId);
}
