package stackdata.coll;

import java.sql.Connection;
import java.sql.DriverManager;
import config.StaticData;


public class ConnectionManager {

	public static Connection conn = null;

	public static Connection getConnection() {
		try {
			if (conn == null) {
				Class.forName(StaticData.Driver_name).newInstance();
				conn = DriverManager.getConnection(StaticData.connectionString);
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return conn;
	}

}
