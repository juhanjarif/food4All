package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML
    private Button loginBtn;

    @FXML
    private Button registerBtn;

    // login 
    @FXML
    private void onLogin(ActionEvent e) {
        go("/fxml/login.fxml", 800, 600);
    }

    // register
    @FXML
    private void onRegister(ActionEvent e) {
        go("/fxml/register.fxml", 800, 600);
    }

    // switching scene helper function
    private void go(String fxml, int width, int height) {
        try {
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
