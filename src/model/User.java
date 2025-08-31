package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String userType;
    private String area;
    private String phoneNumber;
    private String emailOrPhone;
    
    public User(int id, String username, String password, String userType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }
    
    // overloaded constructor w area
    public User(int id, String username, String password, String userType, String area, String phoneNumber, String emailOrPhone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.area = area;
        this.phoneNumber = phoneNumber;
        this.emailOrPhone = emailOrPhone;
    }

    //get set functions
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getUserType() { return userType; }
    public String getArea() { return area; }    
    public String getPhoneNumber() { return phoneNumber; }  
    public String getEmailOrPhone() { return emailOrPhone; } 

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setArea(String area) { this.area = area; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmailOrPhone(String emailOrPhone) { this.emailOrPhone = emailOrPhone; }
}
