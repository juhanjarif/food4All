package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseConnection {

//    private static String getDatabasePath() {
//        // Preferred relative path (db file project er bhitore thakle kaj korbe) ar alada koira locatoin set kora lagbena
//        String relativePath = "resources/data/food4all.db";
//        File dbFile = new File(relativePath);
//
//        if (dbFile.exists()) {
//            return "jdbc:sqlite:" + relativePath;
//        }
//
//        // Abrar
//        File abrarPath = new File("E:/Eclipse IDE launcher/food-for-all/food4All/resources/data/food4all.db");
//        if (abrarPath.exists()) {
//            return "jdbc:sqlite:" + abrarPath.getAbsolutePath();
//        }
//
//        // Mahdeen 
//        File mahdeenPath = new File("E:/Food4All/food4All/resources/data/food4all.db");
//        if (mahdeenPath.exists()) {
//            return "jdbc:sqlite:" + mahdeenPath.getAbsolutePath();
//        }
//
//        // Jarif 
//        File jarifPath = new File("/home/jarif/Desktop/code/java/food-for-all/resources/data/food4all.db");
//        if (jarifPath.exists()) {
//            return "jdbc:sqlite:" + jarifPath.getAbsolutePath();
//        }
//
//        throw new RuntimeException("Database file not found in any known path!");
//    }

//    public static Connection getConnection() throws SQLException {
//        String url = getDatabasePath();
//        System.out.println("Using database at: " + url);
//        return DriverManager.getConnection(url);
//    }

	private static final String URL = "jdbc:sqlite:resources/data/food4all.db"; 

	public static Connection getConnection() throws SQLException {
	    return DriverManager.getConnection(URL);
	}
	
    // SHA-256 hashing method
    public static String sha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing the password", e);
        }
    }
}
