package controller;

import javafx.collections.FXCollections;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Donation;
import model.History;
import dba.DonorDao;

import java.time.LocalDateTime;	
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FoodClaimController {

    @FXML private TableView<Donation> donationTable;
    @FXML private TableColumn<Donation, Integer> colId;
    @FXML private TableColumn<Donation, String> colFoodDetails;
    @FXML private TableColumn<Donation, Integer> colQuantity;
    @FXML private TableColumn<Donation, String> colStatus;
    @FXML
    private TableColumn<Donation, Double> colAmount;
    @FXML private Button claimButton;

    
    private int currentVolunteerId = 1;

    private ObservableList<Donation> donationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // configuring table columns
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colFoodDetails.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFoodDetails()));
        colQuantity.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colStatus.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        colAmount.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getAmount()).asObject());

	
        loadDonations();

        claimButton.setOnAction(e -> claimDonation());
    }

    private void loadDonations() {
        donationList.clear();
        List<Donation> allDonations = DonorDao.getAllDonations();
        for (Donation d : allDonations) {
            if (d.getStatus().equalsIgnoreCase("PENDING")) {
                donationList.add(d);
            }
        }
        donationTable.setItems(donationList);
    }

    private void claimDonation() {
        Donation selected = donationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a donation to claim.").show();
            return;
        }

        
        boolean updated = DonorDao.updateDonationStatus(selected.getId(), "CLAIMED");
        if (!updated) {
            new Alert(Alert.AlertType.ERROR, "Failed to claim donation.").show();
            return;
        }

        //record of history
        History history = new History();
        history.setDonationId(selected.getId());
        history.setVolunteerId(currentVolunteerId);
        history.setDeliveredAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        DonorDao.addHistory(history);

        new Alert(Alert.AlertType.INFORMATION, "Donation claimed successfully!").show();

        // table refresh
        loadDonations();
    }
    
  

}
