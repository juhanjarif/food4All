package model;

public class Donation {

    private int id;
    private int donorId;
    private String donorName; 
    private String foodDetails;
    private int quantity;
    private String status;
    private double amount;
    private String distributionTime; 
    
    public Donation() {
    }
    
    public Donation(int id, int donorId, String donorName, String foodDetails, int quantity, String status, double amount, String distributionTime) {
        this.id = id;
        this.donorId = donorId;
        this.donorName = donorName;
        this.foodDetails = foodDetails;
        this.quantity = quantity;
        this.status = status;
        this.amount = amount;
        this.distributionTime = distributionTime;
    }

    public Donation(int id, int donorId, String foodDetails, int quantity, String status) {
        this.id = id;
        this.donorId = donorId;
        this.foodDetails = foodDetails;
        this.quantity = quantity;
        this.status = status;
    }

    public Donation(int donorId, String foodDetails, int quantity, String status) {
        this.donorId = donorId;
        this.foodDetails = foodDetails;
        this.quantity = quantity;
        this.status = status;
    }
  
    public double getAmount() { 
    	return amount; 
    }
    public void setAmount(double amount) { 
    	this.amount = amount; 
    }


    public int getId() {
        return id;
    }

    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }
    
    public String getDonorName() { 
    	return donorName; 
    } 
    
    public void setDonorName(String donorName) { 
        this.donorName = donorName; 
    }

    public String getFoodDetails() {
        return foodDetails;
    }

    public void setFoodDetails(String foodDetails) {
        this.foodDetails = foodDetails;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDistributionTime() { 
        return distributionTime;
    }
    
    public void setDistributionTime(String distributionTime) {
        this.distributionTime = distributionTime;
    }
}
