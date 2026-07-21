package model;

import model.enums.AdvType;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hierarchical category for classifying advertisements.
 * Matches the backend Category entity structure.
 */
public class Category {
    private Long id;
    private String name;
    private AdvType type;
    private Category parent;
    private List<Category> subCategories = new ArrayList<>();

    public Category() {}

    public Category(Long id, String name, AdvType type, Category parent) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parent = parent;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AdvType getType() { return type; }
    public void setType(AdvType type) { this.type = type; }

    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    public List<Category> getSubCategories() { return subCategories; }
    public void setSubCategories(List<Category> subCategories) { this.subCategories = subCategories; }

    public boolean isRoot() { return parent == null; }

    @Override
    public String toString() {
        return name;
    }
}