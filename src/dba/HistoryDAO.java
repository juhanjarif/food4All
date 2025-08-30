package dba;

import model.History;
import model.User;
import model.Donation;  
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {

    private static final String URL = "jdbc:sqlite:food4all.db";
    private static final String INSERT_HISTORY_QUERY = "INSERT INTO history (user_id, donation_id, claim_date) VALUES (?, ?, ?)";
    private static final String GET_USER_HISTORY_QUERY = "SELECT * FROM history WHERE user_id = ?";

    public void addClaimHistory(User user, Donation donation) {
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(INSERT_HISTORY_QUERY)) {

            stmt.setInt(1, user.getId());
            stmt.setInt(2, donation.getId());
            stmt.setDate(3, new Date(System.currentTimeMillis()));
            stmt.executeUpdate();
        } catch (SQLException e) {
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
                    History historyItem = new History(rs.getInt("id"), rs.getInt("donation_id"), rs.getInt("volunteer_id"), rs.getString("claim_date"));
                    history.add(historyItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
}
