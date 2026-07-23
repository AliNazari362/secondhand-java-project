package com.secondhand.controller;

import com.secondhand.dto.adv.CategoryResponse;
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

/**
 * REST controller for category management operations (admin only).
 *
 * <p>Provides privileged endpoints for creating, updating, deleting, and retrieving categories.
 * All endpoints require admin privileges, verified through the JWT token.
 * Base path: {@code /api/admin/categories}</p>
 */
@RestController
@RequestMapping("api/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    /**
     * Constructs a {@code CategoryController} with the required service dependencies.
     *
     * @param categoryService the service for category business logic
     * @param userService     the service for user lookups used in admin verification
     */
    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    /**
     * Retrieves all categories.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a list of {@link CategoryResponse} objects representing all categories
     */
    @GetMapping
    public List<CategoryResponse> getAllCategories(@RequestHeader("Authorization") String token) {
        checkAdmin(token);
        List<Category> categories = categoryService.getAllCategories();
        return categories.stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single category by its ID.
     *
     * @param id    the ID of the category to retrieve
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return the {@link CategoryResponse} for the requested category
     */
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        Category category = categoryService.getCategoryById(id);
        return toCategoryResponse(category);
    }

    /**
     * Creates a new category.
     *
     * @param category the validated category object to create
     * @param token    the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a {@link ResponseEntity} containing the created {@link CategoryResponse} with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody Category category,
                                                           @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(201).body(toCategoryResponse(created));
    }

    /**
     * Updates an existing category.
     *
     * @param id       the ID of the category to update
     * @param category the validated category object with updated fields
     * @param token    the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a {@link ResponseEntity} containing the updated {@link CategoryResponse}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody Category category,
                                                           @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(toCategoryResponse(updated));
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id    the ID of the category to delete
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a {@link ResponseEntity} with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id,
                                               @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifies that the token belongs to a user with admin privileges.
     *
     * @param token the JWT bearer token to validate
     * @throws ForbiddenException if the user is not an admin
     */
    private void checkAdmin(String token) {
        User admin = userService.findUserById(JwtUtil.getUserIdFromToken(token));
        if (admin.getUserType() != UserType.ADMIN) {
            throw new ForbiddenException("شما دسترسی ادمین برای مدیریت دسته‌بندی‌ها ندارید");
        }
    }

    /**
     * Converts a {@link Category} entity to a {@link CategoryResponse} DTO.
     *
     * @param category the category entity
     * @return the corresponding DTO
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