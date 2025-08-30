package dba;

import model.User;
import model.Donation;
import model.History;
import model.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonorDao {

    // user der data table e add korar jonno
    public static boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, userType) VALUES (?, ?, ?)";
        // DBUtil class e getConnection function ase oikhane URL dewa ase thik moton
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUserType());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Database theke user er data newar jonno
    public static User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("userType")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Donation er data dewar jonno Databse e
    public static boolean addDonation(Donation donation) {
        String sql = "INSERT INTO donations (donorId, foodDetails, quantity, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, donation.getDonorId());
            ps.setString(2, donation.getFoodDetails());
            ps.setInt(3, donation.getQuantity());
            ps.setString(4, donation.getStatus());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Donation> getAllDonations() {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT * FROM donations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Donation(
                        rs.getInt("id"),
                        rs.getInt("donorId"),
                        rs.getString("foodDetails"),
                        rs.getInt("quantity"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Donation er history record history table e dewar jonno
    public static boolean addHistory(History history) {
        String sql = "INSERT INTO history (donationId, volunteerId, deliveredAt) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, history.getDonationId());
            ps.setInt(2, history.getVolunteerId());
            ps.setString(3, history.getDeliveredAt());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
