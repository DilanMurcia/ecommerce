package com.ecommerce.api.dto;

import java.math.BigDecimal;

public record ProductDetailDTO(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        String imageUrl,
        CategoryDTO category) {
}
