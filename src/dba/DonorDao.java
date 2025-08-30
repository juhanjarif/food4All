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
        String sql = "INSERT INTO donations (donorId, foodDetails, quantity, status, amount) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, donation.getDonorId());
            ps.setString(2, donation.getFoodDetails());
            ps.setInt(3, donation.getQuantity());
            ps.setString(4, donation.getStatus());
            ps.setDouble(5, donation.getAmount()); // amount add kora holo
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
        	        rs.getString("status"),
        	        rs.getDouble("amount")
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
    
    public static boolean updateDonationStatus(int donationId, String status) {
        String sql = "UPDATE donations SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, donationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 
    // Ei method diye sob donation er history paoa jabe
 // 'history' ar 'donations' table join kore sob info niye asha hoyeche, jate HistoryLogController e use kora jai

    public static List<History> getAllHistory() {
        List<History> list = new ArrayList<>();
        String sql = "SELECT h.id AS historyId, h.donationId, h.volunteerId, h.deliveredAt, h.amount, d.foodDetails " +
                "FROM history h " +
                "JOIN donations d ON h.donationId = d.id " +
                "ORDER BY h.deliveredAt DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                History history = new History();
                history.setId(rs.getInt("historyId"));
                history.setDonationId(rs.getInt("donationId"));
                history.setVolunteerId(rs.getInt("volunteerId"));
                history.setDeliveredAt(rs.getString("deliveredAt"));
                history.setFoodDetails(rs.getString("foodDetails"));
                history.setAmount(rs.getDouble("amount"));
                list.add(history);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static int addDonationReturnId(Donation donation) {
        String sql = "INSERT INTO donations (donorId, foodDetails, quantity, status, amount) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, donation.getDonorId());
            ps.setString(2, donation.getFoodDetails());
            ps.setInt(3, donation.getQuantity());
            ps.setString(4, donation.getStatus());
            ps.setDouble(5, donation.getAmount()); // amount add kora holo
            int affected = ps.executeUpdate();

            if (affected == 0) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


}
