package com.learnx.controller;

import com.learnx.dto.CategoryDto;
import com.learnx.entity.Category;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.response.Response;
import com.learnx.service.CategoryService;
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
    public Response<?> getAllCategory() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all category successfully!").data(categoryService.getAllCategories()).build();
    }

    @GetMapping("/{categoryId}")
    public Response<?> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get category with id " + categoryId + " successfully!").data(categoryService.getCategoryById(categoryId)).build();
    }

    @PostMapping("")
    public Response<?> createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Tạo danh mục thành công!").data(categoryService.saveCategory(category)).build();
    }

    @PatchMapping("/{categoryId}")
    public Response<?> editCategory(@PathVariable("categoryId") Long categoryId, @RequestBody CategoryDto categoryDto) {
        Optional<Category> categoryOptional = categoryService.getCategoryById(categoryId);
        if (categoryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục!");
        }
        Category category = convertCategoryDTO(categoryDto, categoryOptional);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit category with id " + categoryId + " successfully!").data(categoryService.saveCategory(category)).build();
    }

    @DeleteMapping("/{categoryId}")
    public Response<?> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete category with id " + categoryId + " successfully!").data(categoryService.deleteCategory(categoryId)).build();
    }

    private Category convertCategoryDTO(CategoryDto categoryDto, Optional<Category> categoryOptional) {
        Category category = categoryOptional.get();
        if (categoryDto.getName() != null) category.setName(categoryDto.getName());
        return category;
    }

}
