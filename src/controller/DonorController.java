package controller;

import model.DatabaseConnection;
import java.sql.Connection;

public class DonorController {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Database connected successfully!");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
