package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.DatabaseConnection;

import java.sql.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label status;
    @FXML private RadioButton restaurantRadio;
    @FXML private RadioButton volunteerRadio;
    @FXML private ToggleGroup userTypeGroup;

    @FXML
    private void initialize() {
        // ensuring only either of volunteer or restaurant representatives are being selected as user
        userTypeGroup = new ToggleGroup();
        restaurantRadio.setToggleGroup(userTypeGroup);
        volunteerRadio.setToggleGroup(userTypeGroup);
    }

    @FXML
    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String pass = passwordField.getText();

        if (username.isBlank() || pass.isBlank()) {
            setStatus("Please fillup all fields.");
            return;
        }

        // "admin" for testing
        if (username.equals("admin")) {
            username = "admin@food4all.com";
        }

        String table = restaurantRadio.isSelected() ? "restaurants" : "volunteers";

        //SQL Query to fetch email or phone number that was filled up while registering
        String query = "SELECT id, name, password_hash FROM " + table + " WHERE email_or_phone = ? OR phone_number = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {

        	// both email and phone number being checked
            ps.setString(1, username); 
            ps.setString(2, username); 

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    // comparing w password hash
                    if (hash.equals(DatabaseConnection.sha256(pass))) {
                        setStatus("Welcome, " + rs.getString("name") + "!");
                        go("/fxml/dashboard.fxml", 800, 600);
                        return;
                    }
                }
                setStatus("Invalid username or password.");
            }
        } 
        catch (SQLException ex) {
            setStatus("DB error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // navigating to other screens
    @FXML
    private void onBack(ActionEvent e) {
        go("/fxml/welcome.fxml", 800, 600);
    }
    
    private void go(String fxml, int w, int h) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            stage.setScene(new Scene(root, w, h));
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            setStatus("Failed to load page: " + fxml);
        }
    }

    // method to set status message on the UI
    private void setStatus(String msg) {
        if (status != null) 
        	status.setText(msg);
    }
}
