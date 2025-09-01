package dba;

import model.Donation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VolunteerDAO {

    private static final String URL = 
        "jdbc:sqlite:E:/Eclipse IDE launcher/food4All/resources/data/food4all.db";

    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // k
    public List<Donation> getAvailableDonations() {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donations WHERE status = 'PENDING'";//pending hoilei dekhabe claim korle history te jabega

        System.out.println("=== DEBUG: Starting getAvailableDonations ===");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Database connection successful");
            System.out.println("Connected to: " + conn.getMetaData().getURL());

            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("Row " + count + ": " +
                    "id=" + rs.getInt("id") +
                    ", donorId=" + rs.getInt("donorId") +
                    ", status=" + rs.getString("status") +
                    ", foodDetails=" + rs.getString("foodDetails") +
                    ", quantity=" + rs.getInt("quantity") +
                    ", amount=" + rs.getDouble("amount") +
                    ", distributionTime=" + rs.getString("distributionTime") +
                    ", createdAt=" + rs.getString("createdAt")
                );

                // add to list if you want
                Donation donation = new Donation(
                	    rs.getInt("id"),
                	    rs.getInt("donorId"),
                	    null,
                	    rs.getString("foodDetails"),
                	    rs.getInt("quantity"),
                	    rs.getString("status"),
                	    rs.getString("createdAt"),
                	    rs.getDouble("amount"),
                	    rs.getString("distributionTime") // donor-set expiry
                	);

                donations.add(donation);
            }

            System.out.println("Total rows in donations: " + count);

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== DEBUG: Ending getAvailableDonations ===");
        return donations;
    }


    // Claim a donation
    public boolean claimDonation(Donation donation) {
        String sql = "UPDATE donations SET status = 'claimed', distributionTime = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime distributionTime = LocalDateTime.now().plusHours(1);
            stmt.setString(1, distributionTime.format(FORMATTER));
            stmt.setInt(2, donation.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get next scheduled distribution
    public LocalDateTime getNextDistributionTime() {
        String query = "SELECT distributionTime FROM donations WHERE status = 'claimed' ORDER BY distributionTime ASC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String timeStr = rs.getString("distributionTime");
                if (timeStr != null && !timeStr.isEmpty()) {
                    return LocalDateTime.parse(timeStr, FORMATTER);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Weekly distribution
    public int getWeeklyDistribution(int volunteerId) {
        String sql = "SELECT COUNT(*) AS count FROM history WHERE volunteerId = ? AND deliveredAt >= date('now','-7 days')";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("count");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Monthly distribution
    public int getMonthlyDistribution(int volunteerId) {
        String sql = "SELECT COUNT(*) AS count FROM history WHERE volunteerId = ? AND strftime('%Y-%m', deliveredAt) = strftime('%Y-%m','now')";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("count");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Total distribution
    public int getTotalDistribution(int volunteerId) {
        String sql = "SELECT COUNT(*) AS count FROM history WHERE volunteerId = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("count");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
