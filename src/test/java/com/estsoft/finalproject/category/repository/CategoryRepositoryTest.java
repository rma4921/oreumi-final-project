package com.estsoft.finalproject.category.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.estsoft.finalproject.category.domain.Category;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 이름으로 조회 테스트")
    void findByCategoryName() {
        Category category = new Category("경제");
        categoryRepository.save(category);

        Optional<Category> getCategory = categoryRepository
            .findByCategoryName("경제");
        Optional<Category> isEmpty = categoryRepository
            .findByCategoryName("테스트");

        assertTrue(getCategory.isPresent());
        assertEquals(category.getCategoryName(), getCategory.get().getCategoryName());
        assertThat(isEmpty).isEmpty();
    }
}