package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.DatabaseConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.SessionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;
import dba.VolunteerDAO;
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label status;
    @FXML private RadioButton restaurantRadio;
    @FXML private RadioButton volunteerRadio;
    @FXML private ToggleGroup userTypeGroup;

    @FXML
    private void initialize() {
        // Ensuring only one user type (restaurant/volunteer) is selected
        userTypeGroup = new ToggleGroup();
        restaurantRadio.setToggleGroup(userTypeGroup);
        volunteerRadio.setToggleGroup(userTypeGroup);
    }

    @FXML
    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String pass = passwordField.getText();

        if (username.isBlank() || pass.isBlank()) {
            setStatus("Please fill in all fields.");
            return;
        }

        // **Check if user is admin first**
        if (isAdminUser(username, pass)) {
            // Admin user login successful
            go("/fxml/admin_dashboard.fxml", 800, 600); // Redirect to admin dashboard
            return; // Ensure no further code is executed (i.e., we skip the regular user logic)
        }

        // **Proceed to regular user login (volunteer/restaurant) if not admin**
        loginUser(username, pass); // Handle regular login
    }

    // Hardcoded admin credentials
    private boolean isAdminUser(String username, String password) {
        String adminUsername = "admin"; // Admin username
        String adminPassword = "admin"; // Admin password (you can change it)

        // Check if the entered username and password match the hardcoded admin credentials
        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            // Create an Admin user object and set it in SessionManager
            User adminUser = new User(0, "Admin", password, "admin");
            SessionManager.setCurrentUser(adminUser);
            return true; // Return true if admin login credentials match
        }
        return false; // Return false if not admin
    }

    // Login for volunteers and restaurants
    private void loginUser(String username, String password) {
        boolean isVolunteer = volunteerRadio.isSelected();
        String table = isVolunteer ? "volunteers" : "restaurants";

        try (Connection conn = DatabaseConnection.getConnection()) {
        	//email dite hobe rki
            String sql = "SELECT * FROM " + table + " WHERE email_or_phone = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPasswordHash = rs.getString("password_hash"); // stored hash
                String enteredHash = DatabaseConnection.sha256(password); // hash entered password

                if (dbPasswordHash.equals(enteredHash)) {
                    // login success
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone_number");
                    String email = rs.getString("email_or_phone");
                    String address = rs.getString("address");

                    User loggedInUser = new User(id, name, dbPasswordHash, isVolunteer ? "volunteer" : "restaurant",
                                                 address, phone, email);

                    SessionManager.setCurrentUser(loggedInUser);

                    String dashboardFxml = isVolunteer ? "/fxml/volunteer_dashboard.fxml" : "/fxml/donor_dashboard.fxml";
                    go(dashboardFxml, 800, 600);
                } else {
                    setStatus("Invalid credentials.");
                }
            } else {
                setStatus("User not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            setStatus("Database error: " + ex.getMessage());
        }
    }



    // Navigate to another screen
    private void go(String fxml, int width, int height) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            stage.setScene(new Scene(root, width, height));
        } catch (Exception ex) {
            setStatus("Failed to load page: " + fxml);
        }
    }

    // Helper method to display status messages
    private void setStatus(String msg) {
        if (status != null)
            status.setText(msg);
    }

    @FXML
    private void onBack(ActionEvent e) {
        go("/fxml/welcome.fxml", 800, 600);
    }
}
