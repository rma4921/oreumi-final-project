package com.estsoft.finalproject.content.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.estsoft.finalproject.content.model.ViewedNewsItem;

public interface ViewedNewsItemRepository extends JpaRepository<ViewedNewsItem, Long> {

    public List<ViewedNewsItem> findAll();
}
