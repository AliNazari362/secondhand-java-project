package component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class AdCardController {

    @FXML private Text titleText;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label dateLabel;

    public void setData(Object ad) {
        // TODO: مقداردهی کارت
        System.out.println("Set data called!");
    }
}