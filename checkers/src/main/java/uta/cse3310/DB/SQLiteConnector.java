package uta.cse3310.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector
{
	private static final String URL = "jdbc:sqlite:checkers.db";

	public static Connection connect()
	{
		Connection connection = null;
		
		try
		{
			connection = DriverManager.getConnection(URL);
		}
		catch (SQLException e)
		{
			System.err.println("Error connecting to SQLITE database " + e.getMessage());
		}
		return connection;
	}
	
}
