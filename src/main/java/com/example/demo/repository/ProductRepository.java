package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByIsAvailableTrue();
    List<Product> findByIsFeaturedTrue();
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // Vendor-specific queries
    List<Product> findByVendor(Vendor vendor);
    List<Product> findByVendorId(Long vendorId);
    List<Product> findByVendorAndIsAvailableTrue(Vendor vendor);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.isAvailable = true " +
           "AND p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price ASC")
    List<Product> findByCategoryAndPriceRange(@Param("category") Category category,
                                               @Param("minPrice") Double minPrice,
                                               @Param("maxPrice") Double maxPrice);

    List<Product> findTop10ByOrderByAverageRatingDesc();
    List<Product> findTop10ByOrderByCreatedAtDesc();
}
