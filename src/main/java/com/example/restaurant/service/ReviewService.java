package com.example.restaurant.service;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.Review;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.DishRepository;
import com.example.restaurant.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DishRepository dishRepository;
    // Add a new review for a dish by a user
    public Review addReview(User user, Dish dish, int rating, String comment) {
        Review review = Review.builder()
                .user(user)
                .dish(dish)
                .rating(rating)
                .comment(comment)
                .date(LocalDate.now())
                .build();
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForDish(Long dishId) {
        return reviewRepository.findByDishId(dishId);
    }

    @Transactional
    public Review updateReview(Long id, Integer rating, String comment) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (rating != null) review.setRating(rating);
        if (comment != null) review.setComment(comment);
        // Update the review date to today
        review.setDate(LocalDate.now());

        return reviewRepository.save(review);
    }
    // Delete a review by its ID
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    // Calculate average rating for a dish
    public double getAverageRating(Long dishId) {
        List<Review> reviews = reviewRepository.findByDishId(dishId);
        if (reviews.isEmpty()) return 0.0;

        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }
}