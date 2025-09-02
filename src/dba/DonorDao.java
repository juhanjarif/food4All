package dba;

import model.User;
import java.time.LocalDateTime;

import model.Donation;
import model.History;
import model.DatabaseConnection;
import java.time.format.DateTimeFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DonorDao {
	
	//private static final String URL = "jdbc:sqlite:E:/food4All/resources/data/food4all.db";
	private static final String URL = "jdbc:sqlite:resources/data/food4all.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    // user der data table e add korar jonno
    public static boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, userType) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUserType());
            return ps.executeUpdate() > 0;

        } 
        catch (SQLException e) {
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

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Donation er data dewar jonno Databse e
    public static boolean addDonation(Donation donation) {
        // SQL matches the correct table column order
        String sql = "INSERT INTO donations " +
                     "(donorId, foodDetails, quantity, status, amount, distributionTime, createdAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Formatter matching your DB: YYYY-MM-DD HH:MM:SS
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // createdAt = now
            String createdAt = LocalDateTime.now().format(formatter);
            donation.setCreatedAtString(createdAt);

            // distributionTime = donor-provided expiry, fallback to now if null
            if (donation.getDistributionTime() == null || donation.getDistributionTime().isEmpty()) {
                donation.setDistributionTime(createdAt);
            }

            // Set parameters in exact column order
            ps.setInt(1, donation.getDonorId());                  // donorId
            ps.setString(2, donation.getFoodDetails());          // foodDetails
            ps.setInt(3, donation.getQuantity());                // quantity
            ps.setString(4, donation.getStatus());               // status
            ps.setDouble(5, donation.getAmount());               // amount
            ps.setString(6, donation.getDistributionTime());     // distributionTime
            ps.setString(7, createdAt);                          // createdAt

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }





    public static List<Donation> getAllDonations() {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT d.*, r.name AS donorName " +
                     "FROM donations d " +
                     "JOIN restaurants r ON d.donorId = r.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Donation(
                    rs.getInt("id"),                   
                    rs.getInt("donorId"),              
                    rs.getString("donorName"),         
                    rs.getString("foodDetails"),       
                    rs.getInt("quantity"),            
                    rs.getString("status"),           
                    rs.getString("createdAt"),        
                    rs.getDouble("amount"),            
                    rs.getString("distributionTime")   
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

        }
        catch (SQLException e) {
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
        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
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

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static int addDonationReturnId(Donation donation) {
        String sql = "INSERT INTO donations (donorId, foodDetails, quantity, status, amount, distributionTime, createdAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String createdAt = LocalDateTime.now().format(formatter);

            ps.setInt(1, donation.getDonorId());
            ps.setString(2, donation.getFoodDetails());
            ps.setInt(3, donation.getQuantity());
            ps.setString(4, donation.getStatus());
            ps.setDouble(5, donation.getAmount());

            if (donation.getDistributionTime() == null || donation.getDistributionTime().isEmpty()) {
                donation.setDistributionTime(createdAt);
            }
            ps.setString(6, donation.getDistributionTime()); // distributionTime
            ps.setString(7, createdAt);                     // createdAt
            donation.setCreatedAtString(createdAt);

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

    
    public Donation getDonationByDescription(String description) {
        String sql = "SELECT * FROM donations WHERE (foodDetails || ' (' || quantity || ') - Donor: ' || IFNULL(donorName, '')) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, description);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Donation(
                        rs.getInt("id"),
                        rs.getInt("donorId"),
                        rs.getString("donorName"),
                        rs.getString("foodDetails"),
                        rs.getInt("quantity"),
                        rs.getString("createdAt"),
                        rs.getString("status"),
                        rs.getDouble("amount"),
                        rs.getString("distributionTime")
                );
            }

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public static List<Donation> getDonationsByDonorId(int donorId) {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT d.id, d.donorId, r.name AS donorName, d.foodDetails, d.quantity, d.status, d.amount, d.distributionTime, d.createdAt " +
                     "FROM donations d " +
                     "JOIN restaurants r ON d.donorId = r.id " +
                     "WHERE d.donorId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, donorId); 
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Donation(
                    rs.getInt("id"),
                    rs.getInt("donorId"),
                    rs.getString("donorName"),
                    rs.getString("foodDetails"),
                    rs.getInt("quantity"),
                    rs.getString("createdAt"),
                    rs.getString("status"),
                    rs.getDouble("amount"),
                    rs.getString("distributionTime")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list; 
    }

    
    public Map<String, Integer> getMonthlyDonationStats(int donorId) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String sql = "SELECT strftime('%Y-%m', distributionTime) AS month, COUNT(*) AS count " +
                     "FROM donations WHERE donorId = ? GROUP BY month ORDER BY month ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, donorId);
            ResultSet rs = ps.executeQuery();

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
