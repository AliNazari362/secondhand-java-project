package component;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SearchBarController {

    @FXML private TextField searchField;

    @FXML
    public void onSearch() {
        System.out.println("Search clicked!");
    }
}