package dba;

import model.Donation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VolunteerDAO {

    private static final String URL = "jdbc:sqlite:food4all.db";
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donations;
    }

    public void claimDonation(Donation donation) {
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(CLAIM_DONATION_QUERY)) {

            stmt.setInt(1, donation.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
