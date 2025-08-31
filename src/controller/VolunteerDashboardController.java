package controller;

import dba.VolunteerDAO;
import dba.HistoryDAO;
import dba.DonorDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import model.Donation;
import model.User;
import model.SessionManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class VolunteerDashboardController {

    @FXML private Label volunteerNameLabel;
    @FXML private Label volunteerAreaLabel;
    @FXML private Label volunteerPhoneLabel;
    @FXML private Label volunteerEmailLabel;

    @FXML private Label weeklyDistributionLabel;
    @FXML private Label monthlyDistributionLabel;
    @FXML private Label totalDistributionLabel;

    @FXML private Label remainingTimeLabel;

    @FXML private ListView<String> foodListView;

    @FXML private LineChart<String, Number> performanceChart;

    @FXML private Button claimDonationButton;
    @FXML private Button logoutButton;

    private final VolunteerDAO volunteerDAO = new VolunteerDAO();
    private final HistoryDAO historyDAO = new HistoryDAO();
    private final DonorDao donorDao = new DonorDao();

    @FXML
    public void initialize() {
    	loadVolunteerDetails();
        loadDistributionStats();
        loadRemainingTime();
        loadAvailableDonations();
        loadPerformanceChart();
    }

    private void loadVolunteerDetails() {
        User currentVolunteer = SessionManager.getCurrentUser();
        if (currentVolunteer != null) {
            volunteerNameLabel.setText("Name: " + currentVolunteer.getUsername());
            volunteerAreaLabel.setText("Area: " + currentVolunteer.getArea());
            volunteerPhoneLabel.setText("Phone: " + currentVolunteer.getPhoneNumber());
            volunteerEmailLabel.setText("Email: " + currentVolunteer.getEmailOrPhone());
        }
        else
            System.out.println("No current volunteer found."); 
    }

    private void loadDistributionStats() {
        User currentVolunteer = SessionManager.getCurrentUser();
        if (currentVolunteer == null) return;

        int weekly = historyDAO.getWeeklyDistribution(currentVolunteer.getId());
        int monthly = historyDAO.getMonthlyDistribution(currentVolunteer.getId());
        int total = historyDAO.getTotalDistribution(currentVolunteer.getId());

        weeklyDistributionLabel.setText(String.valueOf(weekly));
        monthlyDistributionLabel.setText(String.valueOf(monthly));
        totalDistributionLabel.setText(String.valueOf(total));
    }

    private void loadRemainingTime() {
        LocalDateTime nextDistribution = volunteerDAO.getNextDistributionTime();
        if (nextDistribution != null) {
            Duration duration = Duration.between(LocalDateTime.now(), nextDistribution);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            remainingTimeLabel.setText(hours + "h " + minutes + "m");
        } 
        else {
            remainingTimeLabel.setText("No scheduled distribution");
        }
    }

    private void loadAvailableDonations() {
        foodListView.getItems().clear();
        List<Donation> donations = donorDao.getAllDonations();
        for (Donation d : donations) {
            foodListView.getItems().add(
                    d.getFoodDetails() + " (" + d.getQuantity() + ") - Donor: " + d.getDonorName()
            );
        }
    }

    private void loadPerformanceChart() {
        performanceChart.getData().clear();
        User currentVolunteer = SessionManager.getCurrentUser();
        if (currentVolunteer == null) 
        	return;

        Map<String, Integer> monthlyStats = historyDAO.getMonthlyPerformance(currentVolunteer.getId());
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Distribution");

        for (Map.Entry<String, Integer> entry : monthlyStats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        performanceChart.getData().add(series);
    }

    @FXML
    private void claimDonation() {
        String selectedItem = foodListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a donation to claim.");
            return;
        }

        Donation donation = donorDao.getDonationByDescription(selectedItem);
        User currentVolunteer = SessionManager.getCurrentUser();

        if (donation != null && currentVolunteer != null) {
            boolean success = volunteerDAO.claimDonation(donation);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Donation claimed successfully!");
                loadAvailableDonations();
                loadDistributionStats();
                loadPerformanceChart();
            }
            else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to claim donation.");
            }
        }
    }

    @FXML
    private void logout() {
        // Clear session
        SessionManager.clearSession();

        // Show logout confirmation
        showAlert(Alert.AlertType.INFORMATION, "Logout", "You have been logged out.");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = loader.load();
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.setScene(new Scene(loginRoot, 800, 600));

            currentStage.show();
        } 
        catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login screen.");
        }
    }



    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
