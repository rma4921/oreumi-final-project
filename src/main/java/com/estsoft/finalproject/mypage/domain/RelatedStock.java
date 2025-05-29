package com.estsoft.finalproject.mypage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RelatedStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relatedStockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_id", nullable = false)
    private ScrappedArticle scrappedArticle;

    @Column(nullable = false)
    private String name;
}
