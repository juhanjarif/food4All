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
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label status;
    @FXML private RadioButton restaurantRadio;
    @FXML private RadioButton volunteerRadio;
    @FXML private Button loginButton;

    @FXML
    private void initialize() {
        // created a toggle group locally
        ToggleGroup userTypeGroup = new ToggleGroup();
        restaurantRadio.setToggleGroup(userTypeGroup);
        volunteerRadio.setToggleGroup(userTypeGroup);
        
        // disabling login button if username or password is empty
        loginButton.disableProperty().bind(
            usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty())
        );
        
        usernameField.requestFocus();
    }

    @FXML
    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (username.isBlank() || pass.isBlank()) {
            setStatus("Please fill in all fields.");
            return;
        }
        loginUser(username, pass); 
    }

    // Login for volunteers and restaurants
    private void loginUser(String username, String password) {
        boolean isVolunteer = volunteerRadio.isSelected();
        String table = isVolunteer ? "volunteers" : "restaurants";

        try (Connection conn = DatabaseConnection.getConnection()) {
        	// sql query fetch for both login method using email and phone number  
        	String sql = "SELECT * FROM " + table + " WHERE email_or_phone = ? OR phone_number = ?";
        	PreparedStatement ps = conn.prepareStatement(sql);
        	ps.setString(1, username);
        	ps.setString(2, username);
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
                } 
                else {
                    setStatus("Invalid credentials.");
                }
            } 
            else {
                setStatus("User not found.");
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
            setStatus("Database error: " + ex.getMessage());
        }
    }

    // navigation to other screen
    private void go(String fxml, int width, int height) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            stage.setScene(new Scene(root, width, height));
        } 
        catch (Exception ex) {
            setStatus("Failed to load page: " + fxml);
        }
    }

    // helper method for status message
    private void setStatus(String msg) {
        if (status != null)
            status.setText(msg);
    }

    @FXML
    private void onBack(ActionEvent e) {
        go("/fxml/welcome.fxml", 800, 600);
    }
}