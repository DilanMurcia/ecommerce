package com.ecommerce.api.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findBySlug(String slug);

    Page<Product> findByCategorySlug(String categorySlug, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByCategorySlugAndNameContainingIgnoreCase(
            String categorySlug, String name, Pageable pageable);

    boolean existsBySlug(String slug);
}
