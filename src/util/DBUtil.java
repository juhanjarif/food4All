package util; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    
	//ami abrar shahriar er jonno jei jaygay rakha db file ta rki khali E: E:\\Eclipse IDE launcher\\food4all\\ part change hobe
	private static final String URL = "jdbc:sqlite:E:\\Eclipse IDE launcher\\food4all\\resources\\Database\\food4all.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
