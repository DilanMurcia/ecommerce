package com.ecommerce.api.dto;

public record CategoryDTO(
        Long id,
        String name,
        String slug,
        String description) {
}
