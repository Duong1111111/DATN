package com.example.DATN.service.interfaces;

import com.example.DATN.dto.request.CategoryRequest;
import com.example.DATN.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAll();
    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Integer categoryId, CategoryRequest request);

    void delete(Integer categoryId);

    CategoryResponse getById(Integer categoryId);
}
