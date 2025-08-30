package model;

public class History {

    private int id;
    private int donationId;
    private int volunteerId;
    private String deliveredAt;
    
    //constructor 
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
    
    //getter and setter function
    public int getId() {
        return id;
    }

    public int getDonationId() {
        return donationId;
    }

    public void setDonationId(int donationId) {
        this.donationId = donationId;
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
}
