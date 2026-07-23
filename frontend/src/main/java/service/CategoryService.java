package service;

import model.Category;
import java.util.Arrays;
import java.util.List;

/**
 * Service class for managing categories.
 * <p>
 * Provides methods for CRUD operations on categories via the backend API.
 * All write operations require admin privileges.
 * </p>
 *
 * @see Category
 * @see ApiClient
 */
public class CategoryService {
    private static final ApiClient api = ApiClient.getInstance();

    /**
     * Retrieves all categories from the backend.
     *
     * @return list of all categories
     * @throws Exception if the request fails
     */
    public static List<Category> getAllCategories() throws Exception {
        String response = api.get("/categories/public");
        Category[] categories = api.fromJson(response, Category[].class);
        return Arrays.asList(categories);
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the category ID
     * @return the category with the given ID
     * @throws Exception if the request fails
     */
    public static Category getCategoryById(Long id) throws Exception {
        String response = api.get("/admin/categories/" + id);
        return api.fromJson(response, Category.class);
    }

    /**
     * Creates a new category.
     *
     * @param category the category to create
     * @return the created category with its ID
     * @throws Exception if the request fails
     */
    public static Category createCategory(Category category) throws Exception {
        String response = api.post("/admin/categories", category);
        return api.fromJson(response, Category.class);
    }

    /**
     * Updates an existing category.
     *
     * @param id       the ID of the category to update
     * @param category the updated category data
     * @return the updated category
     * @throws Exception if the request fails
     */
    public static Category updateCategory(Long id, Category category) throws Exception {
        String response = api.put("/admin/categories/" + id, category);
        return api.fromJson(response, Category.class);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete
     * @throws Exception if the request fails
     */
    public static void deleteCategory(Long id) throws Exception {
        api.delete("/admin/categories/" + id);
    }
}