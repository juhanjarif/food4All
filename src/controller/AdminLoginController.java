package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.SessionManager;
import model.User;

public class AdminLoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label status;
    @FXML private Button loginButton;
    
    @FXML
    private void initialize() {
        // disabling login button if username or password is empty
        loginButton.disableProperty().bind(
            usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty())
        );

        usernameField.requestFocus();
    }

    // on loggin in 
    @FXML
    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isBlank() || password.isBlank()) {
            setStatus("Please fill in all fields.");
            return;
        }

        if (isAdminUser(username, password)) {
            go("/fxml/admin_dashboard.fxml");
        } 
        else {
            setStatus("Invalid admin credentials.");
        }
    }

    // hardcoded username password
    private boolean isAdminUser(String username, String password) {
        String adminUsername = "admin";
        String adminPassword = "admin";

        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            User adminUser = new User(0, "Admin", password, "admin");
            SessionManager.setCurrentUser(adminUser);
            return true;
        }
        return false;
    }

    // navigate to other ui
    private void go(String fxml) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();
        } 
        catch (Exception ex) {
            setStatus("Failed to load page: " + fxml);
        }
    }

    // helper method for status message
    private void setStatus(String msg) {
        if (status != null) status.setText(msg);
    }

    // back to welcome ui button
    @FXML
    private void onBack(ActionEvent e) {
        go("/fxml/welcome.fxml");
    }
}
