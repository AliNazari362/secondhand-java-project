package service;

import model.Category;
import java.util.Arrays;
import java.util.List;

public class CategoryService {

    private final ApiClient api = ApiClient.getInstance();

    public List<Category> getAllCategories() throws Exception {
        String response = api.get("/admin/categories");
        Category[] categories = api.fromJson(response, Category[].class);
        return Arrays.asList(categories);
    }
}