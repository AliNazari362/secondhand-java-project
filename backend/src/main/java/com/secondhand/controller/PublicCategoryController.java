package com.secondhand.controller;

import com.secondhand.dto.category.CategoryResponse;
import com.secondhand.entity.Category;
import com.secondhand.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * کنترلر عمومی برای دریافت دسته‌بندی‌ها (بدون نیاز به احراز هویت)
 */
@RestController
@RequestMapping("api/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * دریافت لیست تمام دسته‌بندی‌ها با parentId
     */
    @GetMapping("/public")
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return categories.stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * تبدیل Category به CategoryResponse
     */
    private CategoryResponse toCategoryResponse(Category category) {
        Long parentId = (category.getParent() != null) ? category.getParent().getId() : null;
        String parentName = (category.getParent() != null) ? category.getParent().getName() : null;
        int subCount = (category.getSubCategories() != null) ? category.getSubCategories().size() : 0;
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                parentId,
                parentName,
                subCount
        );
    }
}