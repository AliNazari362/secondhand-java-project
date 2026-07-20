package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class DashboardController {

    @FXML private TextField searchField;
    @FXML private FlowPane adFlowPane;

    @FXML
    public void onSearch() {
        // TODO: پیاده‌سازی جستجو
        System.out.println("Search clicked!");
    }
}