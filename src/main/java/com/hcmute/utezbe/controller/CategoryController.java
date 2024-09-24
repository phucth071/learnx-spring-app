package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.domain.RequestContext;
import com.hcmute.utezbe.dto.CategoryDto;
import com.hcmute.utezbe.entity.Category;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("")
    public Response getAllCategory() {
        try {
            Long id = RequestContext.getUserId();
            System.out.println("CURRENT USER ID: " + id);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all category successfully!").data(categoryService.getAllCategories()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{categoryId}")
    public Response getCategoryById(@PathVariable("categoryId") Long categoryId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get category with id " + categoryId + " successfully!").data(categoryService.getCategoryById(categoryId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createCategory(@RequestBody CategoryDto categoryDto) {
        try {
            Category category = Category.builder()
                    .name(categoryDto.getName())
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create category successfully!").data(categoryService.saveCategory(category)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/{categoryId}")
    public Response editCategory(@PathVariable("categoryId") Long categoryId, @RequestBody CategoryDto categoryDto) {
        try {
            Optional<Category> categoryOptional = categoryService.getCategoryById(categoryId);
            if (!categoryOptional.isPresent()) {
                throw new ResourceNotFoundException("Category with id " + categoryId + " not found!");
            }
            Category category = categoryOptional.get();
            if (category != null) {
                category = convertCategoryDTO(categoryDto, categoryOptional);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit category with id " + categoryId + " successfully!").data(categoryService.saveCategory(category)).build();
            } else {
                throw new ResourceNotFoundException("Category with id " + categoryId + " not found!");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{categoryId}")
    public Response deleteCategory(@PathVariable("categoryId") Long categoryId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete category with id " + categoryId + " successfully!").data(categoryService.deleteCategory(categoryId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private Category convertCategoryDTO(CategoryDto categoryDto, Optional<Category> categoryOptional) {
        Category category = categoryOptional.get();
        if (categoryDto.getName() != null) category.setName(categoryDto.getName());
        return category;
    }

}
