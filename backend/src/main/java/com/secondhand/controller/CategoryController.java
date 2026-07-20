package com.secondhand.controller;

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

/**
 * REST controller for administrative category management operations.
 *
 * <p>Provides privileged endpoints for managing hierarchical categories.
 * Every endpoint in this controller requires the caller to be an authenticated user
 * with {@link UserType#ADMIN} privileges.
 * Base path: {@code /api/admin/categories}</p>
 *
 * @see Category
 * @see CategoryService
 */
@RestController
@RequestMapping("api/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    /**
     * Constructs a {@code CategoryController} with the required service dependencies.
     *
     * @param categoryService the service containing category management business logic
     * @param userService     the user service used to look up the requesting user for role verification
     */
    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    /**
     * Verifies that the token belongs to a user with {@link UserType#ADMIN} privileges.
     *
     * @param token the JWT bearer token to validate
     * @throws ForbiddenException if the token owner does not have admin privileges
     */
    private void checkAdmin(String token) {
        User admin = userService.findUserById(JwtUtil.getUserIdFromToken(token));
        if (admin.getUserType() != UserType.ADMIN) {
            throw new ForbiddenException("شما دسترسی ادمین برای مدیریت دسته‌بندی‌ها ندارید");
        }
    }

    /**
     * Retrieves a list of all categories in the system, including their hierarchical structure.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a list of all {@link Category} entities
     */
    @GetMapping
    public List<Category> getAllCategories(@RequestHeader("Authorization") String token) {
        checkAdmin(token);
        return categoryService.getAllCategories();
    }

    /**
     * Retrieves a specific category by its ID.
     *
     * @param id    the ID of the category to retrieve
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return the {@link Category} entity with the given ID
     */
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id,
                                    @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        return categoryService.getCategoryById(id);
    }

    /**
     * Creates a new category.
     *
     * @param category the category data to create (must be valid)
     * @param token    the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return the created {@link Category} entity with HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category,
                                                   @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Updates an existing category.
     *
     * @param id       the ID of the category to update
     * @param category the updated category data
     * @param token    the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return the updated {@link Category} entity
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
                                                   @Valid @RequestBody Category category,
                                                   @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id    the ID of the category to delete
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return HTTP 204 (No Content) on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id,
                                               @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}