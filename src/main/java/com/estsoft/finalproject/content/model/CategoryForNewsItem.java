package com.estsoft.finalproject.content.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// A mapping table that connects News Category with Scrapped News Item
@Entity
public class CategoryForNewsItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "news_category_id")
    private NewsCategory newsCategory;
    @ManyToOne
    @JoinColumn(name = "news_item_id")
    private ScrappedNewsItem newsItem;
}
