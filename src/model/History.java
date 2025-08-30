package model;

public class History {

    private int id;
    private int donationId;
    private int volunteerId;
    private String deliveredAt;
    
    public History(){
    	
    }

    public History(int id, int donationId, int volunteerId, String deliveredAt) {
        this.id = id;
        this.donationId = donationId;
        this.volunteerId = volunteerId;
        this.deliveredAt = deliveredAt;
    }

    public History(int donationId, int volunteerId, String deliveredAt) {
        this.donationId = donationId;
        this.volunteerId = volunteerId;
        this.deliveredAt = deliveredAt;
    }

    public int getId() {
        return id;
    }

    public int getDonationId() {
        return donationId;
    }

    public void setDonationId(int donationId) {
        this.donationId = donationId;
    }
    //history id setter add korsi 31/08/25
    public void setId(int id) {
        this.id = id;
    }

    public int getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(String deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    // Ei field e donation er food details rakhbo, jate HistoryLogController e show kora jay
    private String foodDetails;

    public String getFoodDetails() {
        return foodDetails;
    }

    public void setFoodDetails(String foodDetails) {
        this.foodDetails = foodDetails;
    }
    
    private double amount; // amount field add korsi

    public double getAmount() { 
    	return amount; 
    }
    public void setAmount(double amount) { 
    	this.amount = amount; 
    }


}
