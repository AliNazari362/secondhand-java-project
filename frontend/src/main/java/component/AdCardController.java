//package component;
//
//import model.response.AdvertisementSummaryDto;
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
//import javafx.scene.text.Text;
//
//public class AdCardController {
//
//    @FXML private Text titleText;
//    @FXML private Label priceLabel;
//    @FXML private Label cityLabel;
//    @FXML private Label dateLabel;
//
//    public void setData(AdvertisementSummaryDto ad) {
//        titleText.setText(ad.getFullName());
//        priceLabel.setText("💰 قیمت: " + ad.getPrice() + " تومان");
//        cityLabel.setText("📍 " + ad.getCity().name());
//        dateLabel.setText("📅 " + ad.getCreationDate().toLocalDate().toString());
//    }
//}