package controller;

import dba.DonorDao;
import dba.HistoryDAO;
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
    
    @FXML private Label topDonorLabel;
    @FXML private Label topClaimerLabel;
    @FXML private Label totalDonationsLabel;

    @FXML private TableView<History> historyTable;
    @FXML private TableColumn<History, Integer> historyDonationIdCol;
    @FXML private TableColumn<History, String> historyDeliveredAtCol;

    @FXML private BarChart<String, Number> analyticsChart;

    private final DonorDao donorDao = new DonorDao();
    private final HistoryDAO historyDAO = new HistoryDAO();

    @FXML
    public void initialize() {
        loadUserData();
        loadHistoryData();
        loadAnalyticsData();
    }

    // Load volunteers as "users"
    private void loadUserData() {
        List<User> users = getUsers();
        userIdCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        userNameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUsername()));
        userTypeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUserType()));
        userTable.getItems().setAll(users);
    }

    // Fetch volunteers from the database
    private List<User> getUsers() {
        String query = "SELECT * FROM volunteers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<User> users = new java.util.ArrayList<>();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("password_hash"),
                        "volunteer"
                ));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    private void loadHistoryData() {
        // Admin view: load all history
        List<History> histories = historyDAO.getAllHistory();
        historyDonationIdCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getDonationId()).asObject());
        historyDeliveredAtCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDeliveredAt()));
        historyTable.getItems().setAll(histories);
    }

    private void loadAnalyticsData() {
        XYChart.Series<String, Number> donationSeries = new XYChart.Series<>();
        donationSeries.setName("Monthly Donations");

        Map<String, Integer> donationStats = donorDao.getMonthlyDonationStats(0);
        for (Map.Entry<String, Integer> entry : donationStats.entrySet()) {
            donationSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        analyticsChart.getData().clear();
        analyticsChart.getData().add(donationSeries);

        String topDonor = donorDao.getTopDonor();
        String topClaimer = historyDAO.getTopClaimer();
        int totalDonations = donorDao.getTotalDonations();

        topDonorLabel.setText(topDonor != null ? topDonor : "N/A");
        topClaimerLabel.setText(topClaimer != null ? topClaimer : "N/A");
        totalDonationsLabel.setText(String.valueOf(totalDonations));
    }

    @FXML
    private void handleRemoveUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            deleteUser(selectedUser);
            loadUserData();
        }
    }

    private void deleteUser(User user) {
        String query = "DELETE FROM volunteers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
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

    private void resetUserPassword(User user, String newPass) {
        String sql = "UPDATE volunteers SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPass);
            ps.setInt(2, user.getId());
            int updated = ps.executeUpdate();

            if (updated > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Password reset successfully for user: " + user.getUsername());
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed to reset password for user: " + user.getUsername());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleResetPassword() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reset Password");
            dialog.setHeaderText("Reset password for user: " + selectedUser.getUsername());
            dialog.setContentText("Enter new password:");

            dialog.showAndWait().ifPresent(newPass -> {
                if (!newPass.isEmpty()) {
                    resetUserPassword(selectedUser, newPass);
                }
            });
        }
    }
}
