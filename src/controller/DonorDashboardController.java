package controller;

import dba.DonorDao;
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
import java.util.Map;
import javafx.scene.layout.VBox;

public class DonorDashboardController {

    @FXML private Label donorNameLabel;
    @FXML private Label donorAreaLabel;
    @FXML private Label donorPhoneLabel;
    @FXML private Label donorEmailLabel;

    @FXML private Label weeklyDonationLabel;
    @FXML private Label monthlyDonationLabel;
    @FXML private Label totalDonationLabel;

    @FXML private LineChart<String, Number> donationChart;

    @FXML private TableView<Donation> donationTable;
    @FXML private TableColumn<Donation, Integer> colId;
    @FXML private TableColumn<Donation, String> colFoodDetails;
    @FXML private TableColumn<Donation, Integer> colQuantity;
    @FXML private TableColumn<Donation, Double> colAmount;
    @FXML private TableColumn<Donation, String> colStatus;
    @FXML private VBox rootPane;

    @FXML private Button donateButton;
    @FXML private Button logoutButton;

    private final DonorDao donorDao = new DonorDao();
    private final HistoryDAO historyDAO = new HistoryDAO();

    @FXML
    public void initialize() {
        loadDonorDetails();
        loadDonationStats();
        loadDonationChart();
        rootPane.getStylesheets().add(getClass().getResource("/css/donor_dashboard.css").toExternalForm());

    }

    private void loadDonorDetails() {
        User currentDonor = SessionManager.getCurrentUser();
        if (currentDonor != null) {
            donorNameLabel.setText(currentDonor.getUsername());
            donorAreaLabel.setText(currentDonor.getArea());
            donorPhoneLabel.setText(currentDonor.getPhoneNumber());
            donorEmailLabel.setText(currentDonor.getEmailOrPhone());
        } 
        else {
            System.out.println("No current donor found.");
        }
    }

    private void loadDonationStats() {
        User currentDonor = SessionManager.getCurrentUser();
        if (currentDonor == null) return;

        int weekly = historyDAO.getWeeklyDistribution(currentDonor.getId());
        int monthly = historyDAO.getMonthlyDistribution(currentDonor.getId());
        int total = historyDAO.getTotalDistribution(currentDonor.getId());

        weeklyDonationLabel.setText(String.valueOf(weekly));
        monthlyDonationLabel.setText(String.valueOf(monthly));
        totalDonationLabel.setText(String.valueOf(total));
    }

    private void loadDonationChart() {
        donationChart.getData().clear();
        User currentDonor = SessionManager.getCurrentUser();
        if (currentDonor == null) return;

        Map<String, Integer> donationStats = donorDao.getMonthlyDonationStats(currentDonor.getId());
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Donations");

        for (Map.Entry<String, Integer> entry : donationStats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        donationChart.getData().add(series);
    }

    @FXML
    private void openDonationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/donor.fxml"));
            Parent donorRoot = loader.load();
            Stage currentStage = (Stage) donateButton.getScene().getWindow();
            currentStage.setScene(new Scene(donorRoot, 800, 600));
            currentStage.show();
        } 
        catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load donation form.");
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
