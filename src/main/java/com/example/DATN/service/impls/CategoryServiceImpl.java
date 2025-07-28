package com.example.DATN.service.impls;

import com.example.DATN.dto.request.CategoryRequest;
import com.example.DATN.dto.response.CategoryResponse;
import com.example.DATN.entity.Category;
import com.example.DATN.repository.CategoryRepository;
import com.example.DATN.service.interfaces.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(category -> {
            CategoryResponse res = new CategoryResponse();
            res.setCategoryId(category.getCategoryId());
            res.setName(category.getName());
            res.setDescription(category.getDescription());
            res.setStatus(category.getStatus());
            res.setCreatedAt(category.getCreatedAt());
            res.setUpdatedAt(category.getUpdatedAt());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus()!= null ? request.getStatus() : true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category = categoryRepository.save(category);

        CategoryResponse res = new CategoryResponse();
        res.setCategoryId(category.getCategoryId());
        res.setName(category.getName());
        res.setStatus(category.getStatus());
        res.setDescription(category.getDescription());
        res.setCreatedAt(category.getCreatedAt());
        res.setUpdatedAt(category.getUpdatedAt());
        return res;
    }
    @Override
    public CategoryResponse update(Integer categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        if (request.getName() != null) category.setName(request.getName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getStatus() != null) category.setStatus(request.getStatus());
        category.setUpdatedAt(LocalDateTime.now());

        category = categoryRepository.save(category);

        CategoryResponse res = new CategoryResponse();
        res.setCategoryId(category.getCategoryId());
        res.setName(category.getName());
        res.setDescription(category.getDescription());
        res.setStatus(category.getStatus());
        res.setCreatedAt(category.getCreatedAt());
        res.setUpdatedAt(category.getUpdatedAt());

        return res;
    }
    @Override
    public void delete(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }
    @Override
    public CategoryResponse getById(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        CategoryResponse res = new CategoryResponse();
        res.setCategoryId(category.getCategoryId());
        res.setName(category.getName());
        res.setDescription(category.getDescription());
        res.setStatus(category.getStatus());
        res.setCreatedAt(category.getCreatedAt());
        res.setUpdatedAt(category.getUpdatedAt());
        return res;
    }


}