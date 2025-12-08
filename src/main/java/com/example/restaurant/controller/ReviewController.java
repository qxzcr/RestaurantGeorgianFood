package com.example.restaurant.controller;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.Review;
import com.example.restaurant.model.User;
import com.example.restaurant.service.ReviewService;
import com.example.restaurant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews & Ratings", description = "Customer feedback management")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final com.example.restaurant.repository.DishRepository dishRepository;

    @GetMapping("/{dishId}")
    @Operation(summary = "Get reviews for a dish")
    public List<Review> getReviews(@PathVariable Long dishId) {
        return reviewService.getReviewsForDish(dishId);
    }

    @PostMapping("/{dishId}")
    @Operation(summary = "Add a review")
    public Review addReview(@PathVariable Long dishId,
                            @RequestParam String userEmail,
                            @RequestParam int rating,
                            @RequestParam String comment) {
        User user = userService.findByEmail(userEmail);
        Dish dish = dishRepository.findById(dishId).orElseThrow();
        return reviewService.addReview(user, dish, rating, comment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review")
    public Review updateReview(@PathVariable Long id,
                               @RequestParam(required = false) Integer rating,
                               @RequestParam(required = false) String comment) {
        return reviewService.updateReview(id, rating, comment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}