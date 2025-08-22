package com.example.DATN.controller;

import com.example.DATN.dto.request.ReviewRequest;
import com.example.DATN.dto.response.ReviewResponse;
import com.example.DATN.service.interfaces.ReviewService;
import com.example.DATN.utils.enums.responsecode.BaseResponse;
import com.example.DATN.utils.enums.responsecode.SuccessCode;
import org.springframework.http.MediaType;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReviewResponse createReview(@ModelAttribute ReviewRequest request) {
        return reviewService.create(request);
    }
    @PutMapping("/{id}/approve")
    public ResponseEntity<ReviewResponse> approve(@PathVariable Integer id) {
        return ResponseEntity.ok(reviewService.approveReview(id));
    }
    @PutMapping("/{reviewId}/reject")
    public ResponseEntity<BaseResponse<ReviewResponse>> rejectReview(@PathVariable Integer reviewId) {
        ReviewResponse response = reviewService.rejectReview(reviewId);
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, response));
    }
    @GetMapping("/pending")
    public ResponseEntity<List<ReviewResponse>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReviewResponse updateReview(@PathVariable Integer id,
                                       @ModelAttribute ReviewRequest request) {
        return reviewService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
