package com.secondhand.controller;

import com.secondhand.dto.category.CategoryResponse;
import com.secondhand.entity.Category;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserType;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.service.CategoryService;
import com.secondhand.service.JwtUtil;
import com.secondhand.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    private void checkAdmin(String token) {
        User admin = userService.findUserById(JwtUtil.getUserIdFromToken(token));
        if (admin.getUserType() != UserType.ADMIN) {
            throw new ForbiddenException("شما دسترسی ادمین برای مدیریت دسته‌بندی‌ها ندارید");
        }
    }

    // =================== GET all ===================
    @GetMapping
    public List<CategoryResponse> getAllCategories(@RequestHeader("Authorization") String token) {
        checkAdmin(token);
        List<Category> categories = categoryService.getAllCategories();
        return categories.stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    // =================== GET by id ===================
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        Category category = categoryService.getCategoryById(id);
        return toCategoryResponse(category);
    }

    // =================== POST create ===================
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody Category category,
                                                           @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(201).body(toCategoryResponse(created));
    }

    // =================== PUT update ===================
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody Category category,
                                                           @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(toCategoryResponse(updated));
    }

    // =================== DELETE ===================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id,
                                               @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // =================== تبدیل Category به CategoryResponse ===================
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