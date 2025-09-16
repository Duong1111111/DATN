package com.example.DATN.service.impls;

import com.example.DATN.dto.request.ReviewReplyRequest;
import com.example.DATN.dto.request.ReviewRequest;
import com.example.DATN.dto.response.ReviewReplyResponse;
import com.example.DATN.dto.response.ReviewResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.Location;
import com.example.DATN.entity.Review;
import com.example.DATN.entity.ReviewImage;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.LocationRepository;
import com.example.DATN.repository.ReviewRepository;
import com.example.DATN.service.interfaces.ReviewService;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.AccountStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;
    private final LocationRepository locationRepository;
    private final TimeAgoUtil timeAgoUtil;
    private final ImageUploadService imageUploadService;

    public ReviewServiceImpl(ReviewRepository reviewRepository, AccountRepository accountRepository, LocationRepository locationRepository, TimeAgoUtil timeAgoUtil, ImageUploadService imageUploadService) {
        this.reviewRepository = reviewRepository;
        this.accountRepository = accountRepository;
        this.locationRepository = locationRepository;
        this.timeAgoUtil = timeAgoUtil;
        this.imageUploadService = imageUploadService;
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
            res.setAvatar(r.getUser().getAvatar());
            res.setLocationName(r.getLocation().getName());
            res.setImages(
                    r.getImages().stream().map(ReviewImage::getImageUrl).toList()
            );
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
        if (request.getImages() != null) {
            for (MultipartFile file : request.getImages()) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String url = imageUploadService.uploadImage(file, "reviews");
                        ReviewImage ri = new ReviewImage();
                        ri.setImageUrl(url);
                        ri.setReview(r);
                        r.getImages().add(ri);
                    } catch (IOException e) {
                        throw new RuntimeException("Không thể upload ảnh: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        r = reviewRepository.save(r);
        timeAgoUtil.notifyReviewCreated(r);

        return toResponse(r);
    }

    @Override
    public ReviewReplyResponse replyToReview(ReviewReplyRequest request) {
        Review parent = reviewRepository.findById(request.getParentReviewId())
                .orElseThrow(() -> new RuntimeException("Parent review not found"));

        Account user = accountRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review reply = new Review();
        reply.setUser(user);
        reply.setLocation(parent.getLocation()); // reply vẫn thuộc location đó
        reply.setComment(request.getComment());
        reply.setStatus(AccountStatus.ACTIVE);   // reply thì thường active luôn
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());
        reply.setParentReview(parent);
        if (request.getImages() != null) {
            for (MultipartFile file : request.getImages()) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String url = imageUploadService.uploadImage(file, "review_replies");
                        ReviewImage ri = new ReviewImage();
                        ri.setImageUrl(url);
                        ri.setReview(reply);
                        reply.getImages().add(ri);
                    } catch (IOException e) {
                        throw new RuntimeException("Không thể upload ảnh: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        reply = reviewRepository.save(reply);
        parent.getReplies().add(reply);

        return toReplyResponse(reply);
    }

    @Override
    public List<ReviewReplyResponse> getRepliesByParentId(Integer parentReviewId) {
        List<Review> replies = reviewRepository.findByParentReview_ReviewId(parentReviewId);

        return replies.stream()
                .map(this::toReplyResponse)
                .toList();
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
        if (request.getRemoveImageIds() != null && !request.getRemoveImageIds().isEmpty()) {
            r.getImages().removeIf(img -> request.getRemoveImageIds().contains(img.getId()));
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile file : request.getImages()) {
                if (file != null && !file.isEmpty()) { // ✅ check tránh file rỗng
                    try {
                        String url = imageUploadService.uploadImage(file, "reviews");
                        ReviewImage ri = new ReviewImage();
                        ri.setImageUrl(url);
                        ri.setReview(r);
                        r.getImages().add(ri);
                    } catch (IOException e) {
                        throw new RuntimeException("Không thể upload ảnh: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        r = reviewRepository.save(r);

        ReviewResponse res = new ReviewResponse();
        res.setReviewId(r.getReviewId());
        res.setRating(r.getRating());
        res.setComment(r.getComment());
        res.setStatus(r.getStatus());
        res.setUsername(r.getUser().getUsername());
        res.setAvatar(r.getUser().getAvatar());
        res.setLocationName(r.getLocation().getName());
        res.setImages(
                r.getImages().stream().map(ReviewImage::getImageUrl).toList()
        );
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

        return toResponse(r);
    }

    @Override
    public List<ReviewResponse> getAllActive() {
        return reviewRepository.findAllByStatus(AccountStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getActiveByLocation(Integer locationId) {
        return reviewRepository.findByLocation_LocationIdAndStatus(locationId, AccountStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRatingByLocationId(Integer locationId) {
        Double avg = reviewRepository.findAverageRatingByLocationId(locationId);
        return avg != null ? avg : 0.0;
    }

    @Override
    public List<ReviewResponse> getReviewsByLocation(Integer locationId) {
        List<Review> reviews = reviewRepository.findByLocation_LocationId(locationId);

        return reviews.stream().map(r -> {
            ReviewResponse dto = new ReviewResponse();
            dto.setReviewId(r.getReviewId());
            dto.setRating(r.getRating());
            dto.setComment(r.getComment());
            dto.setStatus(r.getStatus());
            dto.setUsername(r.getUser().getUsername());
            dto.setAvatar(r.getUser().getAvatar());
            dto.setLocationName(r.getLocation().getName());
            dto.setImages(r.getImages().stream()
                    .map(ReviewImage::getImageUrl)
                    .toList());
            dto.setCreatedAt(r.getCreatedAt());
            dto.setUpdatedAt(r.getUpdatedAt());
            return dto;
        }).toList();
    }

    private ReviewReplyResponse toReplyResponse(Review review) {
        ReviewReplyResponse res = new ReviewReplyResponse();
        res.setReviewId(review.getReviewId());
        res.setComment(review.getComment());
        res.setUsername(review.getUser().getUsername());
        res.setAvatar(review.getUser().getAvatar());
        res.setImages(
                review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .toList()
        );
        res.setCreatedAt(review.getCreatedAt());
        res.setUpdatedAt(review.getUpdatedAt());
        return res;
    }


    private ReviewResponse toResponse(Review review) {
        ReviewResponse res = new ReviewResponse();
        res.setReviewId(review.getReviewId());
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setStatus(review.getStatus());
        res.setUsername(review.getUser().getUsername());
        res.setAvatar(review.getUser().getAvatar());
        res.setLocationName(review.getLocation().getName());
        res.setImages(
                review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .toList()
        );
        res.setCreatedAt(review.getCreatedAt());
        res.setUpdatedAt(review.getUpdatedAt());
        return res;
    }


}
