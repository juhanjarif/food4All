package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseConnection {

    // url to database file
    private static final String URL = "jdbc:sqlite:/home/jarif/Desktop/code/java/food-for-all/data/food4all.db"; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // SHA-256 hashing method 
    public static String sha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) 
                	hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing the password", e);
        }
    }
}
