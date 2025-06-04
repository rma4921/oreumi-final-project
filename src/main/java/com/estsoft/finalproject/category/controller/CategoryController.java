package com.estsoft.finalproject.category.controller;

import com.estsoft.finalproject.category.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("api/mypage/{scrapId}/categories")
    public String updateTags(@PathVariable Long scrapId,
        @RequestParam(required = false) List<String> categoryNames,
        Model model) {

        categoryService.updateTags(scrapId, categoryNames != null ? categoryNames : List.of());

        model.addAttribute("checkedCategory", categoryNames);
        return "redirect:/mypage/scrap/" + scrapId;
    }
}
