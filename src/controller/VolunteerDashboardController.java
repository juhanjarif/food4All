package controller;

import dba.VolunteerDAO;
import dba.HistoryDAO;
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

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class VolunteerDashboardController {

    @FXML private Label volunteerNameLabel;
    @FXML private Label volunteerAreaLabel;
    @FXML private Label volunteerPhoneLabel;
    @FXML private Label volunteerEmailLabel;

    @FXML private Label weeklyDistributionLabel;
    @FXML private Label monthlyDistributionLabel;
    @FXML private Label totalDistributionLabel;

    @FXML private Label remainingTimeLabel;

    @FXML private ListView<Donation> foodListView;

    @FXML private LineChart<String, Number> performanceChart;

    @FXML private Button claimDonationButton;
    @FXML private Button logoutButton;

    private final VolunteerDAO volunteerDAO = new VolunteerDAO();
    private final HistoryDAO historyDAO = new HistoryDAO();

    @FXML
    public void initialize() {
        loadVolunteerDetails();
        loadDistributionStats();
        loadAvailableDonations();
        loadPerformanceChart();

        foodListView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                updateAvailabilityTime(newItem);  // khali expiry time dekhabe next distribution time oita baad diya disi onek jhamela
            } else {
                remainingTimeLabel.setText("-");
            }
        });
    }

    private void loadVolunteerDetails() {
        User currentVolunteer = SessionManager.getCurrentUser();
        if (currentVolunteer != null) {
            volunteerNameLabel.setText("Name: " + currentVolunteer.getUsername());
            volunteerAreaLabel.setText("Area: " + currentVolunteer.getArea());
            volunteerPhoneLabel.setText("Phone: " + currentVolunteer.getPhoneNumber());
            volunteerEmailLabel.setText("Email: " + currentVolunteer.getEmailOrPhone());
        } else
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

    @SuppressWarnings("static-access")
    private void loadAvailableDonations() {
        foodListView.getItems().clear();

        foodListView.setCellFactory(param -> new ListCell<Donation>() {
            @Override
            protected void updateItem(Donation d, boolean empty) {
                super.updateItem(d, empty);
                if (empty || d == null) {
                    setText(null);
                } else {
                    String donor = (d.getDonorName() == null || d.getDonorName().isEmpty())
                            ? "Donor ID: " + d.getDonorId()
                            : d.getDonorName();
                    setText(d.getFoodDetails() + " (" + d.getQuantity() + ") - " + donor);
                }
            }
        });

        List<Donation> donations = volunteerDAO.getAvailableDonations();

        System.out.println("Found " + donations.size() + " pending donations");
        for (Donation d : donations) {
            System.out.println("Donation: " + d.getFoodDetails() + ", Status: " + d.getStatus());
        }

        foodListView.getItems().setAll(donations);
    }

    private void loadPerformanceChart() {
        performanceChart.getData().clear();
        User currentVolunteer = SessionManager.getCurrentUser();
        if (currentVolunteer == null) 
            return;

        Map<String, Integer> monthlyStats = historyDAO.getMonthlyPerformance(currentVolunteer.getId());
        if (monthlyStats == null || monthlyStats.isEmpty()) {
            performanceChart.setTitle("No distribution data available");
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Distribution");

        for (Map.Entry<String, Integer> entry : monthlyStats.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }

        performanceChart.getData().add(series);
    }

    @FXML
    private void claimDonation() {
        Donation selectedItem = foodListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a donation to claim.");
            return;
        }

        User currentVolunteer = SessionManager.getCurrentUser();

        if (currentVolunteer != null) {
            boolean success = volunteerDAO.claimDonation(selectedItem);
            if (success) {
                historyDAO.addClaimHistory(currentVolunteer, selectedItem);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Donation claimed successfully!");
                loadAvailableDonations();
                loadDistributionStats();
                loadPerformanceChart();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to claim donation.");
            }
        }
    }

    @FXML
    private void logout() {
        SessionManager.clearSession();
        showAlert(Alert.AlertType.INFORMATION, "Logout", "You have been logged out.");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = loader.load();
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.setScene(new Scene(loginRoot, 800, 600));
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login screen.");
        }
    }
    //thik kora hoise updateAvailablity of time
    private void updateAvailabilityTime(Donation donation) {
        LocalDateTime expiry = donation.getExpiry();
        if (expiry != null) {
            Duration remaining = Duration.between(LocalDateTime.now(), expiry);
            if (!remaining.isNegative() && !remaining.isZero()) {
                long days = remaining.toDaysPart();
                long hours = remaining.toHoursPart();
                long minutes = remaining.toMinutesPart();
                remainingTimeLabel.setText(days + "d " + hours + "h " + minutes + "m left");
            } else {
                remainingTimeLabel.setText("Expired");
            }
        } else {
            remainingTimeLabel.setText("Unknown expiry");
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
