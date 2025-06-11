package com.estsoft.finalproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.estsoft.finalproject.content.model.NewsCategory;

public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Long> {
    public List<NewsCategory> findAll();
    public Optional<NewsCategory> findByName(String name);
}
