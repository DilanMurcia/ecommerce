package com.ecommerce.api.product;

import com.ecommerce.api.category.CategoryService;
import com.ecommerce.api.common.ResourceNotFoundException;
import com.ecommerce.api.dto.PagedResponse;
import com.ecommerce.api.dto.ProductDetailDTO;
import com.ecommerce.api.dto.ProductSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public PagedResponse<ProductSummaryDTO> findAll(int page, int size, String categorySlug, String query) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        boolean hasCategory = categorySlug != null && !categorySlug.isBlank();
        boolean hasQuery = query != null && !query.isBlank();

        Page<Product> result;
        if (hasCategory && hasQuery) {
            result = productRepository.findByCategorySlugAndNameContainingIgnoreCase(categorySlug, query.trim(), pageable);
        } else if (hasCategory) {
            result = productRepository.findByCategorySlug(categorySlug, pageable);
        } else if (hasQuery) {
            result = productRepository.findByNameContainingIgnoreCase(query.trim(), pageable);
        } else {
            result = productRepository.findAll(pageable);
        }

        return PagedResponse.from(result, ProductService::toSummary);
    }

    public ProductDetailDTO findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(ProductService::toDetail)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", slug));
    }

    public static ProductSummaryDTO toSummary(Product p) {
        return new ProductSummaryDTO(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getPrice(),
                p.getImageUrl(),
                CategoryService.toDto(p.getCategory()));
    }

    public static ProductDetailDTO toDetail(Product p) {
        return new ProductDetailDTO(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl(),
                CategoryService.toDto(p.getCategory()));
    }
}
