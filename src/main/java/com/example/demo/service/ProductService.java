package com.example.demo.service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.Vendor;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue();
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrue();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Vendor creates product
    @Transactional
    public Product createProductForVendor(Long vendorId, Product product) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!vendor.getIsVerified()) {
            throw new RuntimeException("Vendor must be verified to add products");
        }

        if (!vendor.getIsActive()) {
            throw new RuntimeException("Vendor account is inactive");
        }

        product.setVendor(vendor);
        return productRepository.save(product);
    }

    // Get products by vendor
    public List<Product> getProductsByVendor(Long vendorId) {
        return productRepository.findByVendorId(vendorId);
    }

    // Get available products by vendor
    public List<Product> getAvailableProductsByVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return productRepository.findByVendorAndIsAvailableTrue(vendor);
    }

    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setUnit(product.getUnit());
        existingProduct.setWeight(product.getWeight());
        existingProduct.setIsAvailable(product.getIsAvailable());
        existingProduct.setIsFeatured(product.getIsFeatured());
        existingProduct.setDiscount(product.getDiscount());
        return productRepository.save(existingProduct);
    }

    // Vendor updates their own product
    @Transactional
    public Product updateProductForVendor(Long vendorId, Long productId, Product productData) {
        Product existingProduct = getProductById(productId);

        // Verify product belongs to vendor
        if (existingProduct.getVendor() == null ||
            !existingProduct.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("You can only update your own products");
        }

        if (productData.getName() != null) existingProduct.setName(productData.getName());
        if (productData.getCategory() != null) existingProduct.setCategory(productData.getCategory());
        if (productData.getPrice() != null) existingProduct.setPrice(productData.getPrice());
        if (productData.getStock() != null) existingProduct.setStock(productData.getStock());
        if (productData.getImageUrl() != null) existingProduct.setImageUrl(productData.getImageUrl());
        if (productData.getDescription() != null) existingProduct.setDescription(productData.getDescription());
        if (productData.getBrand() != null) existingProduct.setBrand(productData.getBrand());
        if (productData.getUnit() != null) existingProduct.setUnit(productData.getUnit());
        if (productData.getWeight() != null) existingProduct.setWeight(productData.getWeight());
        if (productData.getIsAvailable() != null) existingProduct.setIsAvailable(productData.getIsAvailable());
        if (productData.getDiscount() != null) existingProduct.setDiscount(productData.getDiscount());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Vendor deletes their own product
    @Transactional
    public void deleteProductForVendor(Long vendorId, Long productId) {
        Product product = getProductById(productId);

        if (product.getVendor() == null ||
            !product.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("You can only delete your own products");
        }

        productRepository.deleteById(productId);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return productRepository.findByCategory(category);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> filterProducts(Long categoryId, Double minPrice, Double maxPrice) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return productRepository.findByCategoryAndPriceRange(category, minPrice, maxPrice);
    }

    public List<Product> getTopRatedProducts() {
        return productRepository.findTop10ByOrderByAverageRatingDesc();
    }

    public List<Product> getNewProducts() {
        return productRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
