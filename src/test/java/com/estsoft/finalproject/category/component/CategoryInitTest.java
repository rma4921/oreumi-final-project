package com.estsoft.finalproject.category.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.estsoft.finalproject.category.domain.Category;
import com.estsoft.finalproject.category.repository.CategoryRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class CategoryInitTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryInit categoryInit;

    @Test
    @DisplayName("지정된 값이 DB에 없는 경우 테스트.")
    void runInitTest() throws Exception {
        categoryRepository.deleteAll();

        categoryInit.run();

        List<String> categoryNames = List.of("정치", "경제", "사회", "생활/문화", "IT/과학", "세계");
        List<String> savedNames = categoryRepository.findAll().stream()
            .map(Category::getCategoryName)
            .toList();

        assertThat(savedNames).containsExactlyInAnyOrderElementsOf(categoryNames);
        assertEquals(savedNames.size(), categoryNames.size());
    }

    @Test
    @DisplayName("지정된 값이 DB에 있는 경우 테스트.")
    void runXTest() throws Exception {
        categoryRepository.deleteAll();
        List<String> initNames = List.of("정치", "경제", "사회");
        categoryRepository.saveAll(
            initNames.stream()
                .map(Category::new)
                .toList()
        );

        categoryInit.run();

        List<String> categoryNames = List.of("정치", "경제", "사회", "생활/문화", "IT/과학", "세계");
        List<String> savedNames = categoryRepository.findAll().stream()
            .map(Category::getCategoryName)
            .toList();

        assertThat(savedNames).containsExactlyInAnyOrderElementsOf(categoryNames);
        assertEquals(savedNames.size(), categoryNames.size());
    }
}