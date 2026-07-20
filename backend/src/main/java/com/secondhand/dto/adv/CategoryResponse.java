package com.secondhand.dto.category;

import com.secondhand.entity.enums.AdvType;

/**
 * Response DTO for returning category information to the client.
 * Used in admin management endpoints to display full category details
 * including its hierarchical structure.
 *
 * @param id              the unique identifier of the category
 * @param name            the display name of the category
 * @param type            the type of the category (PRODUCT or SERVICE)
 * @param parentId        the ID of the parent category, or {@code null} if root
 * @param parentName      the name of the parent category, or {@code null} if root
 * @param subCategoryCount the number of direct sub-categories under this category
 */
public record CategoryResponse(
        Long id,
        String name,
        AdvType type,
        Long parentId,
        String parentName,
        int subCategoryCount
) {}