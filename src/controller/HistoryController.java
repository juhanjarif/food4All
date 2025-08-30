package controller;

import dba.HistoryDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.History;
import model.User;
import java.util.List;

public class HistoryController {

    @FXML
    private ListView<History> historyListView;

    private HistoryDAO historyDAO = new HistoryDAO();

    public void initialize() {
        loadHistory();
    }

    private void loadHistory() {
        List<History> history = historyDAO.getHistoryForUser(getCurrentUser());
        historyListView.getItems().clear();
        historyListView.getItems().addAll(history);
    }

    private User getCurrentUser() {
        // Logic to retrieve current logged-in user
        return new User(1, "John Doe", "password", "volunteer");
    }
}
