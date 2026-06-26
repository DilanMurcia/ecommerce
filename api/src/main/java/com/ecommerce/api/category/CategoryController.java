package com.ecommerce.api.category;

import com.ecommerce.api.dto.CategoryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDTO> list() {
        return categoryService.findAll();
    }

    @GetMapping("/{slug}")
    public CategoryDTO detail(@PathVariable String slug) {
        return categoryService.findBySlug(slug);
    }
}
