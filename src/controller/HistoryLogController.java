package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.History;
import dba.DonorDao;

import java.util.List;

public class HistoryLogController {

    @FXML private TableView<HistoryRecord> historyTable;
    @FXML private TableColumn<HistoryRecord, Integer> colDonationId;
    @FXML private TableColumn<HistoryRecord, Integer> colVolunteerId;
    @FXML private TableColumn<HistoryRecord, String> colDeliveredAt;
    @FXML private TableColumn<HistoryRecord, String> colFoodDetails;

    private ObservableList<HistoryRecord> historyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Table column setup
        colDonationId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getDonationId()).asObject());
        colVolunteerId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getVolunteerId()).asObject());
        colDeliveredAt.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDeliveredAt()));
        colFoodDetails.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFoodDetails()));

        // Load all history
        loadHistory();
    }

    private void loadHistory() {
        historyList.clear();
        List<History> allHistory = DonorDao.getAllHistory(); 

        for (History h : allHistory) {
            historyList.add(new HistoryRecord(
                    h.getDonationId(),
                    h.getVolunteerId(),
                    h.getDeliveredAt(),
                    h.getFoodDetails()
            ));
        }

        historyTable.setItems(historyList);
    }

    // Helper class for TableView
    public static class HistoryRecord {
        private int donationId;
        private int volunteerId;
        private String deliveredAt;
        private String foodDetails;

        public HistoryRecord(int donationId, int volunteerId, String deliveredAt, String foodDetails) {
            this.donationId = donationId;
            this.volunteerId = volunteerId;
            this.deliveredAt = deliveredAt;
            this.foodDetails = foodDetails;
        }

        public int getDonationId() { return donationId; }
        public int getVolunteerId() { return volunteerId; }
        public String getDeliveredAt() { return deliveredAt; }
        public String getFoodDetails() { return foodDetails; }
    }
}
