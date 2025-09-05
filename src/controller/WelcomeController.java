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
    
    @FXML
    private Button adminBtn;

    // login 
    @FXML
    private void onLogin(ActionEvent e) {
        go("/fxml/login.fxml");
    }

    // register
    @FXML
    private void onRegister(ActionEvent e) {
        go("/fxml/register.fxml");
    }
    
    // admin login 
    @FXML
    private void onAdminLogin() {
        go("/fxml/admin_login.fxml");
    }

    // switching scene helper function
    private void go(String fxml) {
        try {
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
