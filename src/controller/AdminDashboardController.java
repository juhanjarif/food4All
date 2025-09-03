package controller;

import dba.DonorDao;
import dba.HistoryDAO;
//import dba.VolunteerDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import model.Donation;
import model.History;
import model.User;
import model.SessionManager;
import model.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class AdminDashboardController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> userIdCol;
    @FXML private TableColumn<User, String> userNameCol;
    @FXML private TableColumn<User, String> userTypeCol;

    @FXML private TableView<Donation> donationTable;
    @FXML private TableColumn<Donation, Integer> donationIdCol;
    @FXML private TableColumn<Donation, String> foodDetailsCol;
    @FXML private TableColumn<Donation, String> donationStatusCol;

    @FXML private TableView<History> historyTable;
    @FXML private TableColumn<History, Integer> historyDonationIdCol;
    @FXML private TableColumn<History, String> historyDeliveredAtCol;

    @FXML private BarChart<String, Number> analyticsChart;

    private final DonorDao donorDao = new DonorDao();
//    private final VolunteerDAO volunteerDAO = new VolunteerDAO();
    private final HistoryDAO historyDAO = new HistoryDAO();

    @FXML
    public void initialize() {
        // Load user data
        loadUserData();
        
        // Load donation data
        loadDonationData();
        
        // Load history data for the current user (this should be handled for admin view as well)
        loadHistoryData();
        
        // Load analytics data (donation stats, etc.)
        loadAnalyticsData();
    }

    // Load all users from the database
    private void loadUserData() {
        List<User> users = getUsers(); // Get users from the database
        userIdCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        userNameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUsername()));
        userTypeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUserType()));
        userTable.getItems().setAll(users);
    }

    // Load all donations from the database
    private void loadDonationData() {
        List<Donation> donations = donorDao.getAllDonations(); // Get all donations
        donationIdCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        foodDetailsCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFoodDetails()));
        donationStatusCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        donationTable.getItems().setAll(donations);
    }

    // Load donation history for the current user (for admin)
    private void loadHistoryData() {
        // Get the current logged-in user (this can be for the admin viewing all history)
        User currentUser = SessionManager.getCurrentUser();
        
        if (currentUser != null) {
            // Fetch history data for the logged-in user
            List<History> histories = historyDAO.getHistoryForUser(currentUser);
            historyDonationIdCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getDonationId()).asObject());
            historyDeliveredAtCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDeliveredAt()));
            historyTable.getItems().setAll(histories);
        } else {
            System.out.println("No user logged in.");
        }
    }

    // Load analytics (donation count, worth, volunteer activity) in a chart
    private void loadAnalyticsData() {
        // Creating a chart for analytics (donations by month)
        XYChart.Series<String, Number> donationSeries = new XYChart.Series<>();
        donationSeries.setName("Donations Analytics");

        // Get monthly donation stats (donorId 1 used here for demonstration)
        Map<String, Integer> donationStats = donorDao.getMonthlyDonationStats(1);
        for (Map.Entry<String, Integer> entry : donationStats.entrySet()) {
            donationSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Clear existing chart data and add the new series
        analyticsChart.getData().clear();
        analyticsChart.getData().add(donationSeries);
    }

    // Fetch all users from the database
    private List<User> getUsers() {
        // SQL query to fetch users
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<User> users = new java.util.ArrayList<>();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("userType")
                ));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    // Handle removing a user from the database (Admin functionality)
    @FXML
    private void handleRemoveUser() {
        // Get the selected user from the table
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Call method to delete user from database
            deleteUser(selectedUser);
            // Reload the user data to reflect changes
            loadUserData();
        }
    }

    // Delete the selected user from the database
    private void deleteUser(User user) {
        // SQL query to delete the user
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, user.getId());
            stmt.executeUpdate(); // Execute the deletion
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Handle logout functionality
    @FXML
    private void handleLogout() {
        // Clear the session (log out the user)
        SessionManager.clearSession();
        
        // Navigate back to the login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = loader.load();
            Stage currentStage = (Stage) userTable.getScene().getWindow();
            currentStage.setScene(new Scene(loginRoot, 800, 600));
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
