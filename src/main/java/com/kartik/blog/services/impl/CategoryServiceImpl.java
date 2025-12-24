package com.kartik.blog.services.impl;


import com.kartik.blog.domain.UpdateCategoryRequest;
import com.kartik.blog.domain.UpdatePostRequest;
import com.kartik.blog.domain.entities.Category;
import com.kartik.blog.repositories.CategoryRepository;
import com.kartik.blog.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> listCategories() {
        return categoryRepository.findAllWithPostCount();
    }

    @Override
    public Category createCategory(Category category) {
        if(categoryRepository.existsByNameIgnoreCase(category.getName())){
            throw new IllegalArgumentException("Category already exists with name");
        }

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {

        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){
            if(category.get().getPosts().size()>0){
                throw new IllegalStateException("Category has posts associated with it");
            }

            categoryRepository.deleteById(id);
        }

    }

    @Override
    public Category getCategoryById(UUID id) {
        return  categoryRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Category not found with id"+id));
    }

    @Override
    @Transactional
    public Category updateCategory(UUID id, UpdateCategoryRequest updateCategoryRequest) {
        Category old =  categoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Category not Found"));

        old.setName(updateCategoryRequest.getName());
        categoryRepository.save(old);

        return old;
    }

}
