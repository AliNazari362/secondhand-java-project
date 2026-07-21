package com.secondhand.controller;

import com.secondhand.entity.Category;
import com.secondhand.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * کنترلر عمومی برای دریافت دسته‌بندی‌ها (بدون نیاز به احراز هویت)
 * این کنترلر برای استفاده در فرم‌های ثبت و ویرایش آگهی در فرانت‌اند طراحی شده است.
 */
@RestController
@RequestMapping("api/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * دریافت لیست تمام دسته‌بندی‌ها (عمومی)
     * @return لیست دسته‌بندی‌ها
     */
    @GetMapping("/public")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }
}