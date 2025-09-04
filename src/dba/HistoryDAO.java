package dba;

import model.History;

import model.User;
import model.DatabaseConnection;
import model.Donation;  
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistoryDAO {
	// fetching the url locally
    private static final String URL = DatabaseConnection.getDatabaseUrl();
    
	private static final String INSERT_HISTORY_QUERY = "INSERT INTO history (volunteerId, donationId, deliveredAt) VALUES (?, ?, ?)";
	private static final String GET_USER_HISTORY_QUERY = "SELECT * FROM history WHERE volunteerId = ?";

    public void addClaimHistory(User user, Donation donation) {
        try (Connection conn = DriverManager.getConnection(URL);
        		PreparedStatement stmt = conn.prepareStatement(INSERT_HISTORY_QUERY)) {

            stmt.setInt(1, user.getId());
            stmt.setInt(2, donation.getId());
            stmt.setDate(3, new Date(System.currentTimeMillis()));
            stmt.executeUpdate();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<History> getHistoryForUser(User user) {
        List<History> history = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
        		PreparedStatement stmt = conn.prepareStatement(GET_USER_HISTORY_QUERY)) {

            stmt.setInt(1, user.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int volunteerId = 0; 
                    try {
                        volunteerId = rs.getInt("volunteerId");
                        if (rs.wasNull()) 
                        	volunteerId = 0;
                    } 
                    catch (SQLException ex) {
                        volunteerId = 0;
                    }
                    History historyItem = new History(
                            rs.getInt("id"),
                            rs.getInt("donation_id"),
                            volunteerId,
                            rs.getString("deliveredAt")
                    );
                    history.add(historyItem);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    // weekly total distribution count
    public int getWeeklyDistribution(int userId) {
        String query = "SELECT COUNT(*) FROM history " + "WHERE volunteerId = ? AND deliveredAt >= date('now', '-7 days')";
        try (Connection conn = DriverManager.getConnection(URL);
        		PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) 
            	return rs.getInt(1);
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // monthly total distribution count
    public int getMonthlyDistribution(int userId) {
        String query = "SELECT COUNT(*) FROM history " + "WHERE volunteerId = ? AND deliveredAt >= date('now', '-30 days')";
        try (Connection conn = DriverManager.getConnection(URL);
        		PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) 
            	return rs.getInt(1);
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public Map<String, Integer> getDailyPerformance(int volunteerId) {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT DATE(distributionTime) as day, SUM(amount) as total " +
                "FROM donations " +
                "WHERE status='PENDING' " +
                "GROUP BY DATE(distributionTime) " +
                "ORDER BY DATE(distributionTime)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("day"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }


    // total distribution count
    public int getTotalDistribution(int userId) {
        String query = "SELECT COUNT(*) FROM history WHERE volunteerId = ?";
        try (Connection conn = DriverManager.getConnection(URL);
        		PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) 
            	return rs.getInt(1);
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // performance chart 
    public Map<String, Integer> getMonthlyPerformance(int userId) {
        String query = "SELECT strftime('%Y-%m', deliveredAt) AS month, COUNT(*) AS count " + "FROM history WHERE volunteerId = ? GROUP BY month ORDER BY month ASC";
        Map<String, Integer> stats = new LinkedHashMap<>();
        try (Connection conn = DriverManager.getConnection(URL);
        		PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("month"), rs.getInt("count"));
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
