package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Donation {

    private int id;
    private int donorId;
    private String donorName;
    private String foodDetails;
    private int quantity;
    private String status;
    private double amount;
    private String distributionTime;
    private String createdAtString;
    private String expiryString; 

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Donation() {
        this.createdAtString = LocalDateTime.now().format(FORMATTER);
    }

    public Donation(int id, int donorId, String donorName, String foodDetails, int quantity,
                    String status, String createdAtString, double amount, String distributionTime) {
        this.id = id;
        this.donorId = donorId;
        this.donorName = donorName;
        this.foodDetails = foodDetails;
        this.quantity = quantity;
        this.status = status;
        this.amount = amount;
        this.distributionTime = distributionTime;
        setCreatedAtString(createdAtString);
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getId() { return id; }
    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getFoodDetails() { return foodDetails; }
    public void setFoodDetails(String foodDetails) { this.foodDetails = foodDetails; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
  
  
    public void setExpiryString(String expiryString) {
        this.expiryString = expiryString;
    }

    public LocalDateTime getExpiry() {
        if (distributionTime != null && !distributionTime.isEmpty()) {
            try {
                return LocalDateTime.parse(distributionTime, FORMATTER);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }


    public void setCreatedAtString(String createdAtString) {
        if (createdAtString != null && !createdAtString.isEmpty()) {
            try {
                LocalDateTime parsed = LocalDateTime.parse(createdAtString, FORMATTER);
                this.createdAtString = parsed.format(FORMATTER);
            } catch (Exception e) {
                this.createdAtString = createdAtString;
            }
        } else {
            this.createdAtString = LocalDateTime.now().format(FORMATTER);
        }
    }

    public LocalDateTime getCreatedAt() {
        if (createdAtString != null && !createdAtString.isEmpty()) {
            try {
                return LocalDateTime.parse(createdAtString, FORMATTER);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String getDistributionTime() { return distributionTime; }
    public void setDistributionTime(String distributionTime) { this.distributionTime = distributionTime; }

    public String getCreatedAtString() { return createdAtString; }
}
