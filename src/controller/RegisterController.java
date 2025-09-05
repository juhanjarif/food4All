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

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private TextField addressField;
    @FXML private RadioButton restaurantRadio;
    @FXML private RadioButton volunteerRadio;
    @FXML private Label status;

    @FXML
    private void initialize() {
        // initializing toggle groups
        ToggleGroup userTypeGroup = new ToggleGroup();
        restaurantRadio.setToggleGroup(userTypeGroup);
        volunteerRadio.setToggleGroup(userTypeGroup);
    }

    // registration while register is selected
    @FXML
    private void onRegister(ActionEvent e) {
        String username = emailField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmField.getText();

        // checking if all fields are filled or not
        if (username.isBlank() || name.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            setStatus("Fill all fields.");
            return;
        }

        // checking if both password and confirm passwords are same or not
        if (!password.equals(confirmPassword)) {
            setStatus("Passwords do not match.");
            return;
        }

        // selecting restaurants or volunteers table for inserting
        String table = restaurantRadio.isSelected() ? "restaurants" : "volunteers";

        try (Connection c = DatabaseConnection.getConnection()) {
            // Check if the email or phone already exists in the database
            PreparedStatement checkStmt = c.prepareStatement(
                    "SELECT COUNT(*) FROM " + table + " WHERE email_or_phone = ?");
            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    setStatus("Username already exists.");
                    return;
                }
            }

            // password hashing
            String hashedPassword = DatabaseConnection.sha256(password);

            // SQL Insert
            String insertSQL = restaurantRadio.isSelected() ? 
                    "INSERT INTO restaurants (name, email_or_phone, address, password_hash, phone_number) VALUES (?, ?, ?, ?, ?)" :
                    "INSERT INTO volunteers (name, email_or_phone, address, password_hash, phone_number) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement insertStmt = c.prepareStatement(insertSQL);
            insertStmt.setString(1, name);
            insertStmt.setString(2, username);
            insertStmt.setString(3, address);
            insertStmt.setString(4, hashedPassword);
            insertStmt.setString(5, phone);

            int rowsAffected = insertStmt.executeUpdate();

            if (rowsAffected > 0) {
                setStatus("Registration successful!");
                go("/fxml/login.fxml");
            } 
            else {
                setStatus("Failed to register.");
            }
        } 
        catch (SQLException ex) {
            setStatus("DB error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onBack(ActionEvent e) {
        go("/fxml/welcome.fxml");
    }

    // helper to navigate
    private void go(String fxml) {
        try {
            Stage stage = (Stage) status.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();
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
