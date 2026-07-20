package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

public class FavoritesController {

    @FXML private FlowPane favoritesFlowPane;

    @FXML
    public void goBack() {
        System.out.println("Go back clicked!");
    }
}