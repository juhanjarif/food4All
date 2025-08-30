package model;

public class Donation {

    private int id;
    private int donorId;
    private String foodDetails;
    private int quantity;
    private String status;
    
    //constructer
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
    
    //getter n setter function
    public int getId() {
        return id;
    }

    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
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
}
