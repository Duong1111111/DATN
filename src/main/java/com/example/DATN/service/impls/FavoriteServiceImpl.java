package com.example.DATN.service.impls;

import com.example.DATN.dto.request.FavoriteRequest;
import com.example.DATN.dto.response.FavoriteResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.Favorite;
import com.example.DATN.entity.Location;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.FavoriteRepository;
import com.example.DATN.repository.LocationRepository;
import com.example.DATN.service.interfaces.FavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
            res.setUserId(fav.getUser().getUserId());
            res.setLocationId(fav.getLocation().getLocationId());
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
        res.setUserId(user.getUserId());
        res.setLocationId(location.getLocationId());
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
        res.setUserId(fav.getUser().getUserId());
        res.setLocationId(fav.getLocation().getLocationId());
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
        res.setUserId(fav.getUser().getUserId());
        res.setLocationId(fav.getLocation().getLocationId());
        res.setStatus(fav.getStatus());
        res.setCreatedAt(fav.getCreatedAt());
        res.setUpdatedAt(fav.getUpdatedAt());

        return res;
    }

}