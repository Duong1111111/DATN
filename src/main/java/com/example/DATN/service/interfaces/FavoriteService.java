package com.example.DATN.service.interfaces;

import com.example.DATN.dto.request.FavoriteRequest;
import com.example.DATN.dto.response.FavoriteResponse;

import java.util.List;

public interface FavoriteService {
    List<FavoriteResponse> getAll();
    FavoriteResponse create(FavoriteRequest request);

    FavoriteResponse update(Integer favorId, FavoriteRequest request);

    void delete(Integer favorId);

    FavoriteResponse getById(Integer favorId);
}