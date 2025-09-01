package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Donation;
import model.History;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dba.DonorDao;
public class DonorController {
	//Form ta banaisi donor.fxml so oigula ana hoilo controller e
    @FXML private TextField foodName, quantity, unit, locationField;
    @FXML private DatePicker preparedAt, expiresAt;
    @FXML private TextArea notes;
    @FXML private Button submitDonation;
    @FXML private Button returnButton; 
    @FXML private TextField amount; 

    private int currentDonorId = 1;

    @FXML
    public void initialize() {
        submitDonation.setOnAction(e -> onSubmit());
    }
    
    //action submit e table e add hoye jabe 
    private void onSubmit() {
        try {
            Donation donation = new Donation();
            donation.setDonorId(currentDonorId);
            donation.setFoodDetails(foodName.getText());	
            donation.setAmount(Double.parseDouble(amount.getText()));
            int qty = Integer.parseInt(quantity.getText());
            String unitStr = unit.getText();
            donation.setQuantity(qty);
            donation.setFoodDetails(foodName.getText() + " (" + unitStr + ")");

            donation.setStatus("PENDING");
            
            LocalDateTime createdAt = LocalDateTime.now();
            donation.setCreatedAtString(createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

          //eita add kora hoise  by abrar
            if (expiresAt.getValue() != null) {
                LocalDateTime expiry = expiresAt.getValue().atStartOfDay(); //eita add kora hoise 
                donation.setDistributionTime(expiry.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                LocalDateTime expiry = createdAt.plusDays(1);
                donation.setDistributionTime(expiry.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

      
            int donationId = DonorDao.addDonationReturnId(donation);
            
            if (donationId > 0) {
                
                History history = new History();
                history.setDonationId(donationId);
                history.setVolunteerId(0);
                history.setDeliveredAt("");
                DonorDao.addHistory(history);

                clearForm();
                new Alert(Alert.AlertType.INFORMATION, "Donation submitted successfully!").show();
            }
            else {
                new Alert(Alert.AlertType.ERROR, "Failed to submit donation").show();
            }

        } 
        catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }
    
    @FXML
    private void returnToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/donor_dashboard.fxml"));
            Parent dashboardRoot = loader.load();
            
            Stage currentStage = (Stage) returnButton.getScene().getWindow();
            currentStage.setScene(new Scene(dashboardRoot, 800, 600));
            currentStage.show();
            
        } 
        catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load donor dashboard.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    //clear korar jonno
    private void clearForm() {
        foodName.clear();
        quantity.clear();
        unit.clear();
        locationField.clear();
        notes.clear();
        preparedAt.setValue(null);
        expiresAt.setValue(null);
    }
}
