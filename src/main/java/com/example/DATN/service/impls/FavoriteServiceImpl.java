package com.example.DATN.service.impls;

import com.example.DATN.dto.request.FavoriteRequest;
import com.example.DATN.dto.response.FavoriteResponse;
import com.example.DATN.dto.response.LocationDetailResponse;
import com.example.DATN.entity.*;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.FavoriteRepository;
import com.example.DATN.repository.LocationRepository;
import com.example.DATN.service.interfaces.FavoriteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final AccountRepository accountRepository;
    private final LocationRepository locationRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, AccountRepository accountRepository, LocationRepository locationRepository) {
        this.favoriteRepository = favoriteRepository;
        this.accountRepository = accountRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public List<FavoriteResponse> getAll() {
        return favoriteRepository.findAll().stream().map(fav -> {
            FavoriteResponse res = new FavoriteResponse();
            res.setFavorId(fav.getFavorId());
            res.setUsername(fav.getUser().getUsername());
            res.setLocationName(fav.getLocation().getName());
            res.setStatus(fav.getStatus());
            res.setCreatedAt(fav.getCreatedAt());
            res.setUpdatedAt(fav.getUpdatedAt());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public FavoriteResponse create(FavoriteRequest request) {
        Favorite fav = new Favorite();

        Account user = accountRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        fav.setUser(user);
        fav.setLocation(location);
        fav.setStatus(request.getStatus() != null ? request.getStatus() : true);
        fav.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
        fav.setUpdatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now());

        fav = favoriteRepository.save(fav);

        FavoriteResponse res = new FavoriteResponse();
        res.setFavorId(fav.getFavorId());
        res.setUsername(user.getUsername());
        res.setLocationName(location.getName());
        res.setStatus(fav.getStatus());
        res.setCreatedAt(fav.getCreatedAt());
        res.setUpdatedAt(fav.getUpdatedAt());

        return res;
    }
    @Override
    public FavoriteResponse update(Integer favorId, FavoriteRequest request) {
        Favorite fav = favoriteRepository.findById(favorId)
                .orElseThrow(() -> new RuntimeException("Favorite not found with id: " + favorId));

        // Optional: cập nhật user nếu cần
        if (request.getUserId() != null) {
            Account user = accountRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            fav.setUser(user);
        }

        // Optional: cập nhật location nếu cần
        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            fav.setLocation(location);
        }

        if (request.getStatus() != null) fav.setStatus(request.getStatus());
        fav.setUpdatedAt(LocalDateTime.now());

        fav = favoriteRepository.save(fav);

        FavoriteResponse res = new FavoriteResponse();
        res.setFavorId(fav.getFavorId());
        res.setUsername(fav.getUser().getUsername());
        res.setLocationName(fav.getLocation().getName());
        res.setStatus(fav.getStatus());
        res.setCreatedAt(fav.getCreatedAt());
        res.setUpdatedAt(fav.getUpdatedAt());

        return res;
    }
    @Override
    public void delete(Integer favorId) {
        if (!favoriteRepository.existsById(favorId)) {
            throw new RuntimeException("Favorite not found with id: " + favorId);
        }
        favoriteRepository.deleteById(favorId);
    }
    @Override
    public FavoriteResponse getById(Integer favorId) {
        Favorite fav = favoriteRepository.findById(favorId)
                .orElseThrow(() -> new RuntimeException("Favorite not found with id: " + favorId));

        FavoriteResponse res = new FavoriteResponse();
        res.setFavorId(fav.getFavorId());
        res.setUsername(fav.getUser().getUsername());
        res.setLocationName(fav.getLocation().getName());
        res.setStatus(fav.getStatus());
        res.setCreatedAt(fav.getCreatedAt());
        res.setUpdatedAt(fav.getUpdatedAt());

        return res;
    }
    @Override
    public FavoriteResponse toggleFavorite(Integer locationId) {
        // 1. Lấy username từ token (SecurityContext)
        String username = getCurrentUsername();
        Account user = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Lấy location theo id
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        // 3. Kiểm tra xem đã có trong danh sách yêu thích chưa
        Optional<Favorite> existingFav = favoriteRepository.findByUserAndLocation(user, location);

        if (existingFav.isPresent()) {
            // Nếu đã có -> xóa khỏi danh sách yêu thích
            favoriteRepository.delete(existingFav.get());

            FavoriteResponse res = new FavoriteResponse();
            res.setFavorId(existingFav.get().getFavorId());
            res.setUsername(user.getUsername());
            res.setLocationName(location.getName());
            res.setStatus(false); // Đã bỏ thích
            res.setCreatedAt(existingFav.get().getCreatedAt());
            res.setUpdatedAt(LocalDateTime.now());

            return res;
        } else {
            // Nếu chưa có -> thêm mới
            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setLocation(location);
            fav.setStatus(true);
            fav.setCreatedAt(LocalDateTime.now());
            fav.setUpdatedAt(LocalDateTime.now());

            fav = favoriteRepository.save(fav);

            FavoriteResponse res = new FavoriteResponse();
            res.setFavorId(fav.getFavorId());
            res.setUsername(user.getUsername());
            res.setLocationName(location.getName());
            res.setStatus(true); // Đã thêm yêu thích
            res.setCreatedAt(fav.getCreatedAt());
            res.setUpdatedAt(fav.getUpdatedAt());

            return res;
        }
    }
    @Override
    public List<FavoriteResponse> getMyFavorites() {
        String username = getCurrentUsername();
        Account user = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Favorite> favorites = favoriteRepository.findByUser(user);

        return favorites.stream().map(fav -> {
            FavoriteResponse res = new FavoriteResponse();
            res.setFavorId(fav.getFavorId());
            res.setUsername(user.getUsername());
            res.setLocationName(fav.getLocation().getName());
            res.setStatus(fav.getStatus());
            res.setCreatedAt(fav.getCreatedAt());
            res.setUpdatedAt(fav.getUpdatedAt());
            return res;
        }).toList();
    }

    @Override
    public LocationDetailResponse getFavoriteLocationDetail(Integer favorId) {
        Favorite fav = favoriteRepository.findById(favorId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        Location loc = fav.getLocation();

        LocationDetailResponse res = new LocationDetailResponse();
        res.setLocationId(loc.getLocationId());
        res.setName(loc.getName());
        res.setDescription(loc.getDescription());
        res.setLocation(loc.getLocation());
        res.setPrice(loc.getPrice());
        res.setOpenTime(loc.getOpenTime());
        res.setCloseTime(loc.getCloseTime());
        res.setImages(loc.getImages() != null
                ? loc.getImages().stream().map(LocationImage::getImageUrl).toList()
                : List.of());
        res.setWebsite(loc.getWebsite());
        res.setPhoneNumber(loc.getPhoneNumber());
        res.setStatus(loc.getStatus());
        res.setCreatedAt(loc.getCreatedAt());
        res.setUpdatedAt(loc.getUpdatedAt());
        res.setCreatedBy(loc.getCreatedBy() != null ? loc.getCreatedBy().getUsername() : null);
        res.setCategories(loc.getCategories() != null
                ? loc.getCategories().stream().map(Category::getName).toList()
                : List.of());

        // Tính rating
        if (loc.getReviews() != null && !loc.getReviews().isEmpty()) {
            double avg = loc.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0.0);
            res.setAverageRating(avg);
            res.setTotalReviews(loc.getReviews().size());
        } else {
            res.setAverageRating(0.0);
            res.setTotalReviews(0);
        }

        return res;
    }


    // Helper method lấy username từ SecurityContext
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return auth.getName();
    }

}