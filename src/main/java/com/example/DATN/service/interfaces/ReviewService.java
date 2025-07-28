package com.example.DATN.service.interfaces;

import com.example.DATN.dto.request.ReviewRequest;
import com.example.DATN.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> getAll();
    ReviewResponse create(ReviewRequest request);

    ReviewResponse update(Integer reviewId, ReviewRequest request);

    void delete(Integer reviewId);

    ReviewResponse getById(Integer reviewId);
}