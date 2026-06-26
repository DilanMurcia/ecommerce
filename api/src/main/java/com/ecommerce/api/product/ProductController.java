package com.ecommerce.api.product;

import com.ecommerce.api.dto.PagedResponse;
import com.ecommerce.api.dto.ProductDetailDTO;
import com.ecommerce.api.dto.ProductSummaryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PagedResponse<ProductSummaryDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q) {
        return productService.findAll(page, size, category, q);
    }

    @GetMapping("/{slug}")
    public ProductDetailDTO detail(@PathVariable String slug) {
        return productService.findBySlug(slug);
    }
}
