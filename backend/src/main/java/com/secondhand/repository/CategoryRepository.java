package com.secondhand.repository;

import com.secondhand.entity.Category;
import com.secondhand.entity.enums.AdvType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Category} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * custom query methods for finding categories by name, type, or parent category.
 * This repository is primarily used by administrators to manage the hierarchical
 * category tree and by the advertisement service to validate category references.</p>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its unique name.
     *
     * @param name the name of the category (e.g., "Electronics")
     * @return an {@link Optional} containing the category if found, or empty if not
     */
    Optional<Category> findByName(String name);

    /**
     * Checks whether a category with the given name already exists.
     *
     * @param name the name to check
     * @return {@code true} if a category with that name exists, {@code false} otherwise
     */
    boolean existsByName(String name);

    /**
     * Returns all categories of the specified type (PRODUCT or SERVICE).
     * Useful for filtering available categories when creating or editing an advertisement.
     *
     * @param type the {@link AdvType} to filter by
     * @return list of categories with the given type; empty list if none found
     */
    List<Category> findByType(AdvType type);

    /**
     * Returns all root-level categories (those with no parent).
     *
     * @return list of root categories; empty list if none found
     */
    List<Category> findByParentIsNull();

    /**
     * Returns all categories that have the specified parent category.
     *
     * @param parentId the ID of the parent category
     * @return list of sub-categories for the given parent; empty list if none found
     */
    List<Category> findByParentId(Long parentId);
}