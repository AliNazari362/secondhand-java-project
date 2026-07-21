package model;

import model.enums.AdvType;
import java.util.ArrayList;
import java.util.List;

public class Category {
    private Long id;
    private String name;
    private AdvType type;
    private Category parent;
    private Long parentId;   // ← اضافه شد
    private List<Category> subCategories = new ArrayList<>();

    public Category() {}

    public Category(Long id, String name, AdvType type, Category parent) {
        this.id = id;
        this.name = name;
        this.type = type;
        setParent(parent); // مقداردهی همزمان parentId
    }

    // getter / setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AdvType getType() { return type; }
    public void setType(AdvType type) { this.type = type; }

    public Category getParent() { return parent; }

    // ✅ مهم: هنگام تنظیم والد، parentId هم مقداردهی می‌شود
    public void setParent(Category parent) {
        this.parent = parent;
        this.parentId = (parent != null) ? parent.getId() : null;
    }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public List<Category> getSubCategories() { return subCategories; }
    public void setSubCategories(List<Category> subCategories) { this.subCategories = subCategories; }

    public boolean isRoot() {
        return parent == null && parentId == null;
    }

    public void addSubCategory(Category subCategory) {
        if (subCategory != null && !this.subCategories.contains(subCategory)) {
            this.subCategories.add(subCategory);
            subCategory.setParent(this);
        }
    }

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