package com.secondhand.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.secondhand.entity.enums.AdvType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a hierarchical category for classifying advertisements.
 *
 * <p>Categories can have a parent-child relationship, allowing for a tree-like
 * structure (e.g., "Electronics" → "Laptops" → "Gaming Laptops").
 * The {@code type} field uses {@link AdvType} to distinguish between
 * categories meant for PRODUCT ads versus SERVICE ads.</p>
 *
 * <p><strong>JSON Serialization Note:</strong> The combination of
 * {@code @JsonManagedReference} and {@code @JsonBackReference} prevents
 * infinite recursion when serializing bidirectional relationships to JSON.
 * The parent side ({@code subCategories}) is serialized normally, while the
 * child side ({@code parent}) is ignored during serialization to break the cycle.</p>
 *
 * @see Adv
 * @see AdvType
 */
@Entity
@Table(
        name = "categories",
        indexes = {
                @Index(name = "idx_category_name", columnList = "name"),
                @Index(name = "idx_category_parent", columnList = "parent_id"),
                @Index(name = "idx_category_type", columnList = "type")
        }
)
public class Category {

    /**
     * Surrogate primary key for this category.
     * Uses database-generated identity for simplicity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Unique, human-readable name of the category (e.g., "Electronics").
     * Must not be blank and is limited to 100 characters.
     */
    @NotBlank(message = "نام دسته‌بندی نمی‌تواند خالی باشد")
    @Size(max = 100, message = "نام دسته‌بندی نباید از ۱۰۰ کاراکتر بیشتر باشد")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Parent category in the hierarchy.
     * If {@code null}, this category is a root-level (top-level) category.
     * This enables the hierarchical (tree) structure.
     *
     * <p><strong>JSON Serialization:</strong> This field is annotated with
     * {@code @JsonBackReference} to prevent infinite recursion when
     * serializing the parent-child relationship.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_category_parent"))
    @JsonBackReference  // <-- این خط مانع از حلقه بی‌نهایت می‌شود
    private Category parent;

    /**
     * List of sub-categories (children) of this category.
     * Maintained as the inverse side of the {@code parent} relationship.
     * Cascades all operations and orphans are removed automatically.
     *
     * <p><strong>JSON Serialization:</strong> This field is annotated with
     * {@code @JsonManagedReference} to allow full serialization of the
     * child list without causing infinite recursion.</p>
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference  // <-- این خط باعث می‌شود زیردسته‌ها نمایش داده شوند
    private List<Category> subCategories = new ArrayList<>();

    /**
     * Discriminator indicating whether this category is for PRODUCT or SERVICE.
     * Reuses the existing {@link AdvType} enum to avoid creating a duplicate enum.
     * Used to filter categories when displaying available options for an advertisement.
     */
    @NotNull(message = "نوع دسته‌بندی نمی‌تواند خالی باشد")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private AdvType type;

    /**
     * JPA-required no-argument constructor.
     */
    public Category() {
    }

    /**
     * Convenience constructor for creating a category with all required fields.
     *
     * @param name   the display name of the category
     * @param type   the type of the category (PRODUCT or SERVICE) from {@link AdvType}
     * @param parent the parent category, or {@code null} for root-level
     */
    public Category(String name, AdvType type, Category parent) {
        this.name = name;
        this.type = type;
        this.parent = parent;
    }

    // ---------- Getters and Setters ----------

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

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public AdvType getType() {
        return type;
    }

    public void setType(AdvType type) {
        this.type = type;
    }

    // ---------- Utility Methods ----------

    /**
     * Adds a sub-category to this category and sets the back-reference.
     * Guards against duplicate entries.
     *
     * @param subCategory the sub-category to add; must not be {@code null}
     */
    public void addSubCategory(Category subCategory) {
        if (subCategory != null && !this.subCategories.contains(subCategory)) {
            this.subCategories.add(subCategory);
            subCategory.setParent(this);
        }
    }

    /**
     * Removes a sub-category from this category and clears its back-reference.
     *
     * @param subCategory the sub-category to remove
     */
    public void removeSubCategory(Category subCategory) {
        if (this.subCategories.remove(subCategory)) {
            subCategory.setParent(null);
        }
    }

    /**
     * Checks whether this category is a root-level category (has no parent).
     *
     * @return {@code true} if this category has no parent, {@code false} otherwise
     */
    public boolean isRoot() {
        return parent == null;
    }

    // ---------- equals, hashCode, toString ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Safe toString that never accesses lazy associations.
     */
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", parent=" + (parent != null ? parent.getName() : "null") +
                '}';
    }
}