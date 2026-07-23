package controller;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;
import model.Category;
import model.enums.AdvType;
import service.CategoryService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;
import utils.Utils;

import java.util.*;

/**
 * Controller for the category management page.
 * Provides a tree view for browsing categories and a form for creating,
 * editing, and deleting categories. Supports hierarchical category structures.
 */
public class CategoryManagementController {

    @FXML
    private TreeView<Category> categoryTreeView;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<Category> parentComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label selectedCategoryLabel;

    private Category selectedCategory;
    private List<Category> allCategories;
    private boolean isUpdating = false;

    /**
     * Initializes the category management view after FXML loading.
     * Sets up combo boxes, tree cell factory, selection listeners, and loads initial data.
     */
    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("PRODUCT", "SERVICE");
        typeComboBox.getSelectionModel().selectFirst();

        categoryTreeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<Category> call(TreeView<Category> param) {
                return new TextFieldTreeCell<>() {
                    @Override
                    public void updateItem(Category item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item.getName() + (item.isRoot() ? " (ریشه)" : ""));
                        }
                    }
                };
            }
        });

        categoryTreeView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (!isUpdating && newVal != null) {
                        selectCategory(newVal.getValue());
                    }
                }
        );

        loadCategories();
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
    }

    /**
     * Loads all categories from the server and builds the category tree.
     * Reconstructs parent-child relationships and sorts the tree alphabetically.
     */
    private void loadCategories() {
        try {
            allCategories = CategoryService.getAllCategories();

            Map<Long, Category> categoryMap = new HashMap<>();
            for (Category cat : allCategories) {
                if (cat.getId() != null) {
                    categoryMap.put(cat.getId(), cat);
                }
            }

            Utils.loadAllCategories(allCategories, categoryMap);
            Platform.runLater(() -> {
                isUpdating = true;
                TreeItem<Category> rootNode = buildTree(allCategories);
                categoryTreeView.setRoot(rootNode);
                categoryTreeView.setShowRoot(false);
                clearForm();
                isUpdating = false;
            });

        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Builds a hierarchical TreeItem structure from a flat list of categories.
     *
     * @param categories the flat list of all categories
     * @return the root TreeItem containing the full category tree
     */
    private TreeItem<Category> buildTree(List<Category> categories) {
        TreeItem<Category> root = new TreeItem<>(new Category(null, "ریشه", AdvType.PRODUCT, null));
        Map<Long, TreeItem<Category>> itemMap = new HashMap<>();

        for (Category cat : categories) {
            if (cat.getId() == null) continue;
            TreeItem<Category> item = new TreeItem<>(cat);
            itemMap.put(cat.getId(), item);
        }

        for (Category cat : categories) {
            if (cat.getId() == null) continue;
            TreeItem<Category> item = itemMap.get(cat.getId());
            if (item == null) continue;

            Category parent = cat.getParent();
            if (parent == null || parent.getId() == null) {
                root.getChildren().add(item);
            } else {
                TreeItem<Category> parentItem = itemMap.get(parent.getId());
                Objects.requireNonNullElse(parentItem, root).getChildren().add(item);
            }
        }

        sortTreeItems(root);
        return root;
    }

    /**
     * Recursively sorts tree items alphabetically by category name.
     *
     * @param item the root tree item to sort
     */
    private void sortTreeItems(TreeItem<Category> item) {
        if (item.getChildren().isEmpty()) return;
        item.getChildren().sort(Comparator.comparing(o -> o.getValue().getName()));
        for (TreeItem<Category> child : item.getChildren()) {
            sortTreeItems(child);
        }
    }

    /**
     * Populates the edit form with the selected category's data.
     *
     * @param category the category selected in the tree view
     */
    private void selectCategory(Category category) {
        this.selectedCategory = category;
        selectedCategoryLabel.setText("دسته بندی انتخاب شده: " + category.getName());
        nameField.setText(category.getName());
        typeComboBox.getSelectionModel().select(category.getType().name());

        if (category.getParent() == null && category.getParentId() != null) {
            for (Category cat : allCategories) {
                if (cat.getId() != null && cat.getId().equals(category.getParentId())) {
                    category.setParent(cat);
                    break;
                }
            }
        }

        populateParentComboBox();
        deleteButton.setDisable(false);
        saveButton.setDisable(false);
    }

    /**
     * Populates the parent category combo box with all categories except the selected one.
     * Selects the current parent of the selected category if available.
     */
    private void populateParentComboBox() {
        parentComboBox.getItems().clear();
        parentComboBox.getItems().add(null);

        if (allCategories == null) return;

        for (Category cat : allCategories) {
            if (selectedCategory == null || !cat.getId().equals(selectedCategory.getId())) {
                parentComboBox.getItems().add(cat);
            }
        }

        if (selectedCategory != null) {
            Category parent = selectedCategory.getParent();
            parentComboBox.getSelectionModel().select(parent);
        } else {
            parentComboBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Clears the category edit form and resets selection state.
     */
    private void clearForm() {
        selectedCategory = null;
        selectedCategoryLabel.setText("هیچ دسته بندی انتخاب نشده است.");
        nameField.clear();
        typeComboBox.getSelectionModel().selectFirst();
        parentComboBox.getItems().clear();
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
    }

    /**
     * Saves the category form data, creating a new category or updating the selected one.
     * Uses a shallow parent reference to prevent circular references and stack overflow.
     */
    @FXML
    public void onSave() {
        try {
            String name = nameField.getText().trim();
            String typeStr = typeComboBox.getSelectionModel().getSelectedItem();
            Category parent = parentComboBox.getSelectionModel().getSelectedItem();

            if (name.isEmpty()) {
                AlertUtil.showError("لطفاً نام دسته بندی را وارد کنید.");
                return;
            }
            if (typeStr == null || typeStr.isEmpty()) {
                AlertUtil.showError("لطفاً نوع دسته بندی را انتخاب کنید.");
                return;
            }

            AdvType type = AdvType.valueOf(typeStr);
            Category category = new Category();
            category.setName(name);
            category.setType(type);

            if (parent != null) {
                Category shallowParent = new Category();
                shallowParent.setId(parent.getId());
                category.setParent(shallowParent);
            } else {
                category.setParent(null);
            }

            category.setSubCategories(null);

            if (selectedCategory == null) {
                CategoryService.createCategory(category);
                AlertUtil.showSuccess("دسته بندی با موفقیت ایجاد شد.");
            } else {
                category.setId(selectedCategory.getId());
                CategoryService.updateCategory(selectedCategory.getId(), category);
                AlertUtil.showSuccess("دسته بندی با موفقیت ویرایش شد.");
            }

            loadCategories();

        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Deletes the selected category after user confirmation.
     * Shows a warning if the category may have subcategories.
     * Reloads the category tree on success.
     */
    @FXML
    public void onDelete() {
        if (selectedCategory == null) {
            AlertUtil.showError("لطفاً یک دسته بندی را انتخاب کنید.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
                "حذف دسته بندی",
                "آیا از حذف دسته بندی '" + selectedCategory.getName() + "' اطمینان دارید؟\n" +
                        "توجه: اگر این دسته بندی زیردسته داشته باشد، عملیات با خطا مواجه می شود."
        );
        if (!confirm) return;

        try {
            CategoryService.deleteCategory(selectedCategory.getId());
            AlertUtil.showSuccess("دسته بندی با موفقیت حذف شد.");
            clearForm();
            loadCategories();
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Clears the form and deselects the current tree selection.
     */
    @FXML
    public void onClear() {
        clearForm();
        categoryTreeView.getSelectionModel().clearSelection();
    }

    /**
     * Prepares the form for creating a new root-level category.
     */
    @FXML
    public void onAddRoot() {
        clearForm();
        selectedCategoryLabel.setText("ایجاد دسته بندی جدید (ریشه)");
        nameField.clear();
        typeComboBox.getSelectionModel().selectFirst();
        populateParentComboBox();
        saveButton.setDisable(false);
        deleteButton.setDisable(true);
    }

    /**
     * Navigates back to the admin panel page.
     */
    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.ADMIN, null);
    }
}