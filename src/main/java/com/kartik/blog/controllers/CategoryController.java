package com.kartik.blog.controllers;

import com.kartik.blog.domain.UpdateCategoryRequest;
import com.kartik.blog.domain.dtos.CategoryDto;
import com.kartik.blog.domain.dtos.CreateCategoryRequest;
import com.kartik.blog.domain.dtos.UpdateCategoryRequestDto;
import com.kartik.blog.domain.entities.Category;
import com.kartik.blog.mappers.CategoryMapper;
import com.kartik.blog.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryDto>>  listCategories(){
        List<CategoryDto> categories =categoryService.listCategories()
                .stream().map(categoryMapper::toDto)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto
            ){

        if (!id.equals(updateCategoryRequestDto.getId())) {
            throw new IllegalArgumentException("Path ID and body ID must match");
        }

        UpdateCategoryRequest updatecategoryRequest = categoryMapper.toUpdatecategoryRequest(updateCategoryRequestDto);
        Category category = categoryService.updateCategory(id, updatecategoryRequest);
        CategoryDto dto = categoryMapper.toDto(category);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest )
    {
        Category categoryToCreate = categoryMapper.toEntity(createCategoryRequest);
        Category savedCategory = categoryService.createCategory(categoryToCreate);

        return new ResponseEntity<>(
                categoryMapper.toDto(savedCategory),
                HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id){
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
