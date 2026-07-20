package component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CategoryChipController {

    @FXML private Label categoryLabel;

    public void setCategory(Object category) {
        System.out.println("Set category called!");
    }
}