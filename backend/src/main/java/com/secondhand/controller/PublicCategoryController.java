package com.secondhand.controller;

import com.secondhand.dto.adv.CategoryResponse;
import com.secondhand.entity.Category;
import com.secondhand.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Public REST controller for retrieving category information without authentication.
 *
 * <p>Provides a single public endpoint to fetch all categories.
 * Base path: {@code /api/categories}</p>
 */
@RestController
@RequestMapping("api/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    /**
     * Constructs a {@code PublicCategoryController} with the required service dependency.
     *
     * @param categoryService the category service used to retrieve category data
     */
    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all categories.
     *
     * @return a list of {@link CategoryResponse} objects representing all categories
     */
    @GetMapping("/public")
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return categories.stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
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