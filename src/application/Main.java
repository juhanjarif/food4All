package application;

import controller.FoodClaimController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            //donation form run korar jonno nicher ta
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/donor.fxml"));
            
            //volunteer form dekhar jonno
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Volunteer.fxml"));
            Parent root = loader.load();

            Scene volunteerScene = new Scene(root, 550, 400);

            primaryStage.setTitle("Volunteer Claim Donations");
            primaryStage.setScene(volunteerScene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
