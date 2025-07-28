package com.example.DATN.controller;

import com.example.DATN.dto.request.ReviewRequest;
import com.example.DATN.dto.response.ReviewResponse;
import com.example.DATN.service.interfaces.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewResponse> getAllReviews() {
        return reviewService.getAll();
    }

    @GetMapping("/{id}")
    public ReviewResponse getReviewById(@PathVariable Integer id) {
        return reviewService.getById(id);
    }

    @PostMapping
    public ReviewResponse createReview(@RequestBody ReviewRequest request) {
        return reviewService.create(request);
    }

    @PutMapping("/{id}")
    public ReviewResponse updateReview(@PathVariable Integer id, @RequestBody ReviewRequest request) {
        return reviewService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
