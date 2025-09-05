package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/welcome.fxml"));
        	Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle("food4All - A nextCRabrar Project");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.show();

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
