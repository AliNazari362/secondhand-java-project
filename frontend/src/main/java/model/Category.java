package model;

import model.enums.AdvType;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hierarchical category for classifying advertisements.
 * <p>
 * Categories can have a parent-child relationship, allowing for a tree-like
 * structure (e.g., "Electronics" → "Laptops" → "Gaming Laptops").
 * The {@code type} field uses {@link AdvType} to distinguish between
 * categories meant for PRODUCT ads versus SERVICE ads.
 * </p>
 */
public class Category {
    private Long id;
    private String name;
    private AdvType type;
    private Category parent;
    private Long parentId;          // ← برای دریافت از JSON (backend معمولاً parentId می‌فرستد)
    private List<Category> subCategories = new ArrayList<>();

    public Category() {
    }

    public Category(Long id, String name, AdvType type, Category parent) {
        this.id = id;
        this.name = name;
        this.type = type;
        setParent(parent); // مقداردهی همزمان parentId
    }

    // ===== Getters & Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AdvType getType() {
        return type;
    }

    public void setType(AdvType type) {
        this.type = type;
    }

    public Category getParent() {
        return parent;
    }

    /**
     * Sets the parent category and also updates parentId accordingly.
     */
    public void setParent(Category parent) {
        this.parent = parent;
        if (parent != null) {
            this.parentId = parent.getId();
        } else {
            this.parentId = null;
        }
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    /**
     * Checks whether this category is a root-level category (has no parent).
     */
    public boolean isRoot() {
        return parent == null && parentId == null;
    }

    /**
     * Adds a sub-category to this category and sets the back-reference.
     */
    public void addSubCategory(Category subCategory) {
        if (subCategory != null && !this.subCategories.contains(subCategory)) {
            this.subCategories.add(subCategory);
            subCategory.setParent(this);
        }
    }

    /**
     * Removes a sub-category from this category and clears its back-reference.
     */
    public void removeSubCategory(Category subCategory) {
        if (this.subCategories.remove(subCategory)) {
            subCategory.setParent(null);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}