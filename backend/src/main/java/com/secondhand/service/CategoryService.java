package com.secondhand.service;

import com.secondhand.entity.Category;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ResourceNotFoundException;
import com.secondhand.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for managing hierarchical categories.
 * Provides CRUD operations for categories, used exclusively by administrators.
 *
 * @see Category
 * @see CategoryRepository
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Returns all categories in the system.
     *
     * @return list of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Finds a category by its ID.
     *
     * @param id the category ID
     * @return the found category
     * @throws ResourceNotFoundException if no category exists with the given ID
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("دسته‌بندی با این شناسه یافت نشد"));
    }

    /**
     * Creates a new category.
     *
     * @param category the category to create
     * @return the saved category
     * @throws BadRequestException if a category with the same name already exists
     */
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new BadRequestException("دسته‌بندی با این نام قبلاً ثبت شده است");
        }
        return categoryRepository.save(category);
    }

    /**
     * Updates an existing category.
     *
     * @param id             the ID of the category to update
     * @param categoryDetails the new category data
     * @return the updated category
     * @throws ResourceNotFoundException if no category exists with the given ID
     * @throws BadRequestException       if the new name conflicts with an existing category
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        Category existing = getCategoryById(id);

        // بررسی تکراری نبودن نام (در صورت تغییر نام)
        if (!existing.getName().equals(categoryDetails.getName()) &&
                categoryRepository.existsByName(categoryDetails.getName())) {
            throw new BadRequestException("دسته‌بندی با این نام قبلاً ثبت شده است");
        }

        existing.setName(categoryDetails.getName());
        existing.setType(categoryDetails.getType());
        existing.setParent(categoryDetails.getParent());
        return categoryRepository.save(existing);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete
     * @throws ResourceNotFoundException if no category exists with the given ID
     */
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        // (اختیاری) می‌توانید بررسی کنید که این دسته‌بندی زیردسته یا آگهی نداشته باشد
        categoryRepository.delete(category);
    }
}