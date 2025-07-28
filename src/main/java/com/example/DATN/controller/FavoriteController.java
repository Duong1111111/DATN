package com.example.DATN.controller;

import com.example.DATN.dto.request.FavoriteRequest;
import com.example.DATN.dto.response.FavoriteResponse;
import com.example.DATN.service.interfaces.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<FavoriteResponse> getAllFavorites() {
        return favoriteService.getAll();
    }
    @GetMapping("/{id}")
    public FavoriteResponse getFavoriteById(@PathVariable Integer id) {
        return favoriteService.getById(id);
    }

    @PostMapping
    public FavoriteResponse createFavorite(@RequestBody FavoriteRequest request) {
        return favoriteService.create(request);
    }

    @PutMapping("/{id}")
    public FavoriteResponse updateFavorite(@PathVariable Integer id, @RequestBody FavoriteRequest request) {
        return favoriteService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Integer id) {
        favoriteService.delete(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}
