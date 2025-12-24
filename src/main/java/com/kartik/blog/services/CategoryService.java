package com.kartik.blog.services;

import com.kartik.blog.domain.UpdateCategoryRequest;
import com.kartik.blog.domain.entities.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> listCategories();
    Category createCategory(Category category);
    void deleteCategory(UUID id);
    Category getCategoryById(UUID id);
    Category updateCategory(UUID id, UpdateCategoryRequest updateCategoryRequest);
}
