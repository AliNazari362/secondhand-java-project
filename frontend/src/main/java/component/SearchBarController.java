//package component;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.TextField;
//
//public class SearchBarController {
//
//    @FXML private TextField searchField;
//
//    private SearchListener listener;
//
//    public interface SearchListener {
//        void onSearch(String keyword);
//    }
//
//    public void setListener(SearchListener listener) {
//        this.listener = listener;
//    }
//
//    @FXML
//    public void onSearch() {
//        if (listener != null) {
//            listener.onSearch(searchField.getText().trim());
//        }
//    }
//}