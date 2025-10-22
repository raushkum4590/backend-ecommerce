package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public List<Review> getProductReviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return reviewRepository.findByProduct(product);
    }

    public List<Review> getUserReviews(User user) {
        return reviewRepository.findByUser(user);
    }

    @Transactional
    public Review addReview(User user, Long productId, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if user already reviewed this product
        if (reviewRepository.findByProductAndUser(product, user).isPresent()) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);

        Review savedReview = reviewRepository.save(review);

        // Update product average rating
        updateProductRating(product);

        return savedReview;
    }

    @Transactional
    public Review updateReview(Long reviewId, Integer rating, String comment) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        review.setRating(rating);
        review.setComment(comment);
        Review updatedReview = reviewRepository.save(review);

        // Update product average rating
        updateProductRating(review.getProduct());

        return updatedReview;
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Product product = review.getProduct();
        reviewRepository.delete(review);

        // Update product average rating
        updateProductRating(product);
    }

    private void updateProductRating(Product product) {
        List<Review> reviews = reviewRepository.findByProduct(product);
        if (reviews.isEmpty()) {
            product.setAverageRating(0.0);
            product.setReviewCount(0);
        } else {
            double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            product.setAverageRating(Math.round(avgRating * 10.0) / 10.0);
            product.setReviewCount(reviews.size());
        }
        productRepository.save(product);
    }
}

