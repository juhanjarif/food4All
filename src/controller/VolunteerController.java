package controller;

import dba.VolunteerDAO;
import dba.HistoryDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.Donation;
import model.User;
import java.util.List;

public class VolunteerController {

    @FXML
    private ListView<Donation> foodListView;

    private VolunteerDAO volunteerDAO = new VolunteerDAO();
    private HistoryDAO historyDAO = new HistoryDAO();

    public void initialize() {
        loadAvailableDonations();
    }

    private void loadAvailableDonations() {
        List<Donation> donations = volunteerDAO.getAvailableDonations();
        foodListView.getItems().clear();
        foodListView.getItems().addAll(donations);
    }

    @FXML
    public void claimDonation() {
        Donation selectedDonation = foodListView.getSelectionModel().getSelectedItem();
        if (selectedDonation != null) {
            User currentUser = getCurrentUser(); 
            historyDAO.addClaimHistory(currentUser, selectedDonation);
            volunteerDAO.claimDonation(selectedDonation);
            showAlert("Donation Claimed", "You have successfully claimed the donation!");
            loadAvailableDonations();
        } 
        else {
            showAlert("No Selection", "Please select a donation to claim.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private User getCurrentUser() {
        // Logic to retrieve current logged-in user
        return new User(1, "John Doe", "password", "volunteer");
    }
}
