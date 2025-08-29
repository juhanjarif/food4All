package controller;

import util.DBUtil;
import java.sql.Connection;

public class DonorController {
    public static void main(String[] args) {
        try (Connection conn = DBUtil.getConnection()) {
            if (conn != null) {
                System.out.println("Database connected successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
