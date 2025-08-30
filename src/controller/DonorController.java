package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Donation;
import model.History;
import dba.DonorDao;

import java.sql.*;
public class DonorController {
	
	//Form ta banaisi donor.fxml so oigula ana hoilo controller e
    @FXML private TextField foodName, quantity, unit, locationField;
    @FXML private DatePicker preparedAt, expiresAt;
    @FXML private TextArea notes;
    @FXML private Button submitDonation;
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

      
            int donationId = DonorDao.addDonationReturnId(donation);
            
            if (donationId > 0) {
                
                History history = new History();
                history.setDonationId(donationId);
                history.setVolunteerId(0);
                history.setDeliveredAt("");
                DonorDao.addHistory(history);

                clearForm();
                new Alert(Alert.AlertType.INFORMATION, "Donation submitted successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to submit donation").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
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
