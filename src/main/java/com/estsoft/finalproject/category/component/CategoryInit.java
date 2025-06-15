package com.estsoft.finalproject.category.component;

import com.estsoft.finalproject.category.domain.Category;
import com.estsoft.finalproject.category.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryInit implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        List<String> categoryNames = List.of("정치", "경제", "사회", "생활/문화", "IT/과학", "세계");
        for (String categoryName : categoryNames) {
            categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
        }
    }
}
