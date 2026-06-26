package com.ecommerce.api.category;

import com.ecommerce.api.common.ResourceNotFoundException;
import com.ecommerce.api.dto.CategoryDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(CategoryService::toDto)
                .toList();
    }

    public CategoryDTO findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(CategoryService::toDto)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", slug));
    }

    public static CategoryDTO toDto(Category c) {
        return new CategoryDTO(c.getId(), c.getName(), c.getSlug(), c.getDescription());
    }
}
