package com.ecommerce.api.config;

import com.ecommerce.api.category.Category;
import com.ecommerce.api.category.CategoryRepository;
import com.ecommerce.api.product.Product;
import com.ecommerce.api.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("!test")
@Order(1)
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (categoryRepository.count() > 0 || productRepository.count() > 0) {
            log.info("DataSeeder: data already present, skipping (categories={}, products={})",
                    categoryRepository.count(), productRepository.count());
            return;
        }

        Map<String, String> categoryData = new LinkedHashMap<>();
        categoryData.put("T-Shirts", "Custom printed t-shirts");
        categoryData.put("Hoodies", "Cozy printed hoodies");
        categoryData.put("Mugs", "Ceramic mugs with your designs");
        categoryData.put("Posters", "High-quality printed posters");
        categoryData.put("Stickers", "Vinyl stickers for laptops and more");

        Map<String, Category> categories = new LinkedHashMap<>();
        categoryData.forEach((name, description) -> {
            String slug = Category.slugify(name);
            Category c = new Category(name, slug, description);
            categories.put(name, categoryRepository.save(c));
        });
        log.info("DataSeeder: seeded {} categories", categories.size());

        record SeedProduct(String name, BigDecimal price, String description) {}

        Map<String, List<SeedProduct>> productsByCategory = new LinkedHashMap<>();
        productsByCategory.put("T-Shirts", List.of(
                new SeedProduct("Classic T-Shirt", new BigDecimal("19.99"),
                        "Soft cotton tee with a relaxed fit. Perfect for everyday wear."),
                new SeedProduct("Premium T-Shirt", new BigDecimal("24.99"),
                        "Heavyweight ringspun cotton for a superior feel and durability."),
                new SeedProduct("Graphic T-Shirt", new BigDecimal("22.99"),
                        "Bold printed graphics on a comfy crew neck tee."),
                new SeedProduct("Vintage T-Shirt", new BigDecimal("21.99"),
                        "Distressed look tee with a lived-in vintage vibe.")));
        productsByCategory.put("Hoodies", List.of(
                new SeedProduct("Classic Hoodie", new BigDecimal("34.99"),
                        "Fleece-lined pullover hoodie with kangaroo pocket."),
                new SeedProduct("Premium Hoodie", new BigDecimal("39.99"),
                        "Heavyweight pullover with a brushed interior for extra warmth."),
                new SeedProduct("Zip-Up Hoodie", new BigDecimal("37.99"),
                        "Full-zip hoodie with side pockets and a metal YKK zipper."),
                new SeedProduct("Pullover Hoodie", new BigDecimal("32.99"),
                        "Minimalist pullover hoodie with a clean, modern cut.")));
        productsByCategory.put("Mugs", List.of(
                new SeedProduct("Classic Mug", new BigDecimal("14.99"),
                        "11oz ceramic mug, microwave and dishwasher safe."),
                new SeedProduct("Travel Mug", new BigDecimal("17.99"),
                        "Insulated stainless steel travel mug that keeps drinks hot for hours."),
                new SeedProduct("Latte Mug", new BigDecimal("15.99"),
                        "Tall ceramic latte mug with a curved handle."),
                new SeedProduct("Ceramic Mug", new BigDecimal("16.99"),
                        "Glossy white ceramic mug with vibrant full-wrap print.")));
        productsByCategory.put("Posters", List.of(
                new SeedProduct("Classic Poster", new BigDecimal("18.99"),
                        "Premium matte paper poster, 18x24 inches."),
                new SeedProduct("Vintage Poster", new BigDecimal("22.99"),
                        "Aged-look poster printed on heavyweight archival paper."),
                new SeedProduct("Minimalist Poster", new BigDecimal("19.99"),
                        "Clean line-art poster for a modern aesthetic."),
                new SeedProduct("Abstract Poster", new BigDecimal("16.99"),
                        "Vibrant abstract design printed in rich color.")));
        productsByCategory.put("Stickers", List.of(
                new SeedProduct("Classic Sticker", new BigDecimal("4.99"),
                        "Durable vinyl sticker with a glossy finish, 3x3 inches."),
                new SeedProduct("Vinyl Sticker", new BigDecimal("6.99"),
                        "Weatherproof vinyl sticker that lasts outdoors."),
                new SeedProduct("Laptop Sticker", new BigDecimal("5.99"),
                        "Die-cut sticker perfect for laptops and water bottles."),
                new SeedProduct("Bumper Sticker", new BigDecimal("7.99"),
                        "UV-resistant bumper sticker, 5x3 inches.")));

        int totalProducts = productsByCategory.entrySet().stream()
                .mapToInt(entry -> {
                    Category category = categories.get(entry.getKey());
                    int created = 0;
                    for (SeedProduct sp : entry.getValue()) {
                        String slug = Product.slugify(sp.name());
                        String imageUrl = buildImageUrl(sp.name());
                        Product p = new Product(sp.name(), slug, sp.description(), sp.price(), imageUrl, category);
                        productRepository.save(p);
                        created++;
                    }
                    return created;
                })
                .sum();
        log.info("DataSeeder: seeded {} products across {} categories", totalProducts, categories.size());
    }

    private static String buildImageUrl(String productName) {
        String text = URLEncoder.encode(productName, StandardCharsets.UTF_8).replace("+", "%20");
        return "https://placehold.co/600x600/EEE/333?text=" + text;
    }
}
