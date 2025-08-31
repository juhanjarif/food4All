package dba;

import model.Donation;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

public class VolunteerDAO {

    //private static final String URL = "jdbc:sqlite:food4all.db";
	
	// JARIF
	private static final String URL = "jdbc:sqlite:/home/jarif/Desktop/code/java/food-for-all/resources/data/food4all.db";
    
	private static final String GET_DONATIONS_QUERY = "SELECT * FROM donations WHERE status = 'available'";
    private static final String CLAIM_DONATION_QUERY = "UPDATE donations SET status = 'claimed' WHERE id = ?";

    public List<Donation> getAvailableDonations() {
        List<Donation> donations = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(GET_DONATIONS_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Donation donation = new Donation(rs.getInt("id"), rs.getInt("donorId"), rs.getString("foodDetails"), rs.getInt("quantity"), rs.getString("status"));
                donations.add(donation);
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return donations;
    }

    public boolean claimDonation(Donation donation) {
        String query = CLAIM_DONATION_QUERY;
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donation.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public LocalDateTime getNextDistributionTime() {
        String query = "SELECT distributionTime FROM donations " +
                       "WHERE status = 'claimed' ORDER BY distributionTime ASC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String timeStr = rs.getString("distributionTime"); // should be in ISO format (e.g. 2025-08-31T15:00:00)
                return LocalDateTime.parse(timeStr);
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // weekly distribution count -- volunteer
    public int getWeeklyDistribution(int volunteerId) {
        String sql = "SELECT COUNT(*) AS count FROM history " +
                     "WHERE volunteerId = ? AND deliveredAt >= date('now','-7 days')";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("count");

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // monthly distribution count -- volunteer
    public int getMonthlyDistribution(int volunteerId) {
        String sql = "SELECT COUNT(*) AS count FROM history " +
                     "WHERE volunteerId = ? AND strftime('%Y-%m', deliveredAt) = strftime('%Y-%m','now')";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("count");

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // total distribution count -- volunteer
    public int getTotalDistribution(int volunteerId) {
        String sql = "SELECT COUNT(*) AS count FROM history WHERE volunteerId = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("count");

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

}
