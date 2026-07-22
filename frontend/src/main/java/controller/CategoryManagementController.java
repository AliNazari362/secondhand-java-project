package controller;

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

import java.util.*;

public class CategoryManagementController {

    @FXML private TreeView<Category> categoryTreeView;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<Category> parentComboBox;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;
    @FXML private Label selectedCategoryLabel;

    private final CategoryService categoryService = new CategoryService();
    private Category selectedCategory;
    private List<Category> allCategories;
    private boolean isUpdating = false;

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

    private void loadCategories() {
        try {
            allCategories = categoryService.getAllCategories();

            Map<Long, Category> categoryMap = new HashMap<>();
            for (Category cat : allCategories) {
                if (cat.getId() != null) {
                    categoryMap.put(cat.getId(), cat);
                }
            }

            for (Category cat : allCategories) {
                Long pid = cat.getParentId();
                if (pid != null) {
                    Category parent = categoryMap.get(pid);
                    if (parent != null) {
                        cat.setParent(parent);
                        if (!parent.getSubCategories().contains(cat)) {
                            parent.getSubCategories().add(cat);
                        }
                    }
                }
            }

            Platform.runLater(() -> {
                isUpdating = true;
                TreeItem<Category> rootNode = buildTree(allCategories);
                categoryTreeView.setRoot(rootNode);
                categoryTreeView.setShowRoot(false);
                clearForm();
                isUpdating = false;
            });

        } catch (Exception e) {
            AlertUtil.showError("خطا در بارگذاری دسته‌بندی‌ها: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
                if (parentItem != null) {
                    parentItem.getChildren().add(item);
                } else {
                    root.getChildren().add(item);
                }
            }
        }

        sortTreeItems(root);
        return root;
    }

    private void sortTreeItems(TreeItem<Category> item) {
        if (item.getChildren().isEmpty()) return;
        item.getChildren().sort(Comparator.comparing(o -> o.getValue().getName()));
        for (TreeItem<Category> child : item.getChildren()) {
            sortTreeItems(child);
        }
    }

    private void selectCategory(Category category) {
        this.selectedCategory = category;
        selectedCategoryLabel.setText("دسته‌بندی انتخاب‌شده: " + category.getName());
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
            if (parent != null) {
                parentComboBox.getSelectionModel().select(parent);
            } else {
                parentComboBox.getSelectionModel().select(null);
            }
        } else {
            parentComboBox.getSelectionModel().selectFirst();
        }
    }

    private void clearForm() {
        selectedCategory = null;
        selectedCategoryLabel.setText("هیچ دسته‌بندی انتخاب نشده است.");
        nameField.clear();
        typeComboBox.getSelectionModel().selectFirst();
        parentComboBox.getItems().clear();
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
    }

    // ============================================================
    // ✅ متد onSave اصلاح‌شده (رفع StackOverflow)
    // ============================================================
    @FXML
    public void onSave() {
        try {
            String name = nameField.getText().trim();
            String typeStr = typeComboBox.getSelectionModel().getSelectedItem();
            Category parent = parentComboBox.getSelectionModel().getSelectedItem();

            if (name.isEmpty()) {
                AlertUtil.showError("لطفاً نام دسته‌بندی را وارد کنید.");
                return;
            }
            if (typeStr == null || typeStr.isEmpty()) {
                AlertUtil.showError("لطفاً نوع دسته‌بندی را انتخاب کنید.");
                return;
            }

            AdvType type = AdvType.valueOf(typeStr);
            Category category = new Category();
            category.setName(name);
            category.setType(type);

            // ---------- ایجاد والد سبک (فقط با id) برای جلوگیری از حلقه ----------
            if (parent != null) {
                Category shallowParent = new Category();
                shallowParent.setId(parent.getId());
                category.setParent(shallowParent);
            } else {
                category.setParent(null);
            }

            // برای اطمینان، `subCategories` را خالی بگذارید (یا null)
            category.setSubCategories(null);

            if (selectedCategory == null) {
                categoryService.createCategory(category);
                AlertUtil.showSuccess("دسته‌بندی با موفقیت ایجاد شد.");
            } else {
                category.setId(selectedCategory.getId());
                categoryService.updateCategory(selectedCategory.getId(), category);
                AlertUtil.showSuccess("دسته‌بندی با موفقیت ویرایش شد.");
            }

            loadCategories();

        } catch (Exception e) {
            AlertUtil.showError("خطا در ذخیره دسته‌بندی: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================================

    @FXML
    public void onDelete() {
        if (selectedCategory == null) {
            AlertUtil.showError("لطفاً یک دسته‌بندی را انتخاب کنید.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
                "حذف دسته‌بندی",
                "آیا از حذف دسته‌بندی '" + selectedCategory.getName() + "' اطمینان دارید؟\n" +
                        "توجه: اگر این دسته‌بندی زیردسته داشته باشد، عملیات با خطا مواجه می‌شود."
        );
        if (!confirm) return;

        try {
            categoryService.deleteCategory(selectedCategory.getId());
            AlertUtil.showSuccess("دسته‌بندی با موفقیت حذف شد.");
            clearForm();
            loadCategories();
        } catch (Exception e) {
            AlertUtil.showError("خطا در حذف دسته‌بندی: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onClear() {
        clearForm();
        categoryTreeView.getSelectionModel().clearSelection();
    }

    @FXML
    public void onAddRoot() {
        clearForm();
        selectedCategoryLabel.setText("ایجاد دسته‌بندی جدید (ریشه)");
        nameField.clear();
        typeComboBox.getSelectionModel().selectFirst();
        populateParentComboBox();
        saveButton.setDisable(false);
        deleteButton.setDisable(true);
    }

    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.ADMIN, null);
    }
}