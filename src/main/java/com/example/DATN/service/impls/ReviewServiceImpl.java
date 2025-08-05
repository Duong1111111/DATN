package com.example.DATN.service.impls;

import com.example.DATN.dto.request.ReviewRequest;
import com.example.DATN.dto.response.ReviewResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.Location;
import com.example.DATN.entity.Review;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.LocationRepository;
import com.example.DATN.repository.ReviewRepository;
import com.example.DATN.service.interfaces.ReviewService;
import com.example.DATN.utils.enums.options.AccountStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;
    private final LocationRepository locationRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, AccountRepository accountRepository, LocationRepository locationRepository) {
        this.reviewRepository = reviewRepository;
        this.accountRepository = accountRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public List<ReviewResponse> getAll() {
        return reviewRepository.findAll().stream().map(r -> {
            ReviewResponse res = new ReviewResponse();
            res.setReviewId(r.getReviewId());
            res.setRating(r.getRating());
            res.setComment(r.getComment());
            res.setStatus(r.getStatus());
            res.setUsername(r.getUser().getUsername());
            res.setLocationName(r.getLocation().getName());
            res.setCreatedAt(r.getCreatedAt());
            res.setUpdatedAt(r.getUpdatedAt());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public ReviewResponse create(ReviewRequest request) {
        Review r = new Review();

        Account user = accountRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        r.setUser(user);
        r.setLocation(location);
        r.setRating(request.getRating());
        r.setComment(request.getComment());
        r.setStatus(AccountStatus.PENDING);
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());

        r = reviewRepository.save(r);

        ReviewResponse res = new ReviewResponse();
        res.setReviewId(r.getReviewId());
        res.setRating(r.getRating());
        res.setComment(r.getComment());
        res.setStatus(r.getStatus());
        res.setUsername(r.getUser().getUsername());
        res.setLocationName(r.getLocation().getName());
        res.setCreatedAt(r.getCreatedAt());
        res.setUpdatedAt(r.getUpdatedAt());

        return res;
    }
    @Override
    public ReviewResponse approveReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (review.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reviews can be approved.");
        }

        review.setStatus(AccountStatus.ACTIVE);
        review.setUpdatedAt(LocalDateTime.now());

        return toResponse(reviewRepository.save(review));
    }
    @Override
    public ReviewResponse rejectReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (review.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reviews can be rejected.");
        }

        review.setStatus(AccountStatus.INACTIVE);
        review.setUpdatedAt(LocalDateTime.now());

        return toResponse(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> getPendingReviews() {
        return reviewRepository.findAllByStatus(AccountStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public ReviewResponse update(Integer reviewId, ReviewRequest request) {
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        if (request.getUserId() != null) {
            Account user = accountRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            r.setUser(user);
        }

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            r.setLocation(location);
        }

        if (request.getRating() != null) r.setRating(request.getRating());
        if (request.getComment() != null) r.setComment(request.getComment());
        if (request.getStatus() != null) r.setStatus(request.getStatus());
        r.setUpdatedAt(LocalDateTime.now());

        r = reviewRepository.save(r);

        ReviewResponse res = new ReviewResponse();
        res.setReviewId(r.getReviewId());
        res.setRating(r.getRating());
        res.setComment(r.getComment());
        res.setStatus(r.getStatus());
        res.setUsername(r.getUser().getUsername());
        res.setLocationName(r.getLocation().getName());
        res.setCreatedAt(r.getCreatedAt());
        res.setUpdatedAt(r.getUpdatedAt());

        return res;
    }

    @Override
    public void delete(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
    @Override
    public ReviewResponse getById(Integer reviewId) {
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        ReviewResponse res = new ReviewResponse();
        res.setReviewId(r.getReviewId());
        res.setRating(r.getRating());
        res.setComment(r.getComment());
        res.setStatus(r.getStatus());
        res.setUsername(r.getUser().getUsername());
        res.setLocationName(r.getLocation().getName());
        res.setCreatedAt(r.getCreatedAt());
        res.setUpdatedAt(r.getUpdatedAt());

        return res;
    }

    private ReviewResponse toResponse(Review review) {
        ReviewResponse res = new ReviewResponse();
        res.setReviewId(review.getReviewId());
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setStatus(review.getStatus());
        res.setUsername(review.getUser().getUsername());
        res.setLocationName(review.getLocation().getName());
        res.setCreatedAt(review.getCreatedAt());
        res.setUpdatedAt(review.getUpdatedAt());
        return res;
    }


}
