package com.ecommerce.api.dto;

import java.math.BigDecimal;

public record ProductSummaryDTO(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        String imageUrl,
        CategoryDTO category) {
}
