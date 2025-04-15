package uta.cse3310.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DB
{
	public static void createTable()
	{
		String createStatement = "CREATE TABLE IF NOT EXISTS USERS (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL UNIQUE,\n"
                + "rank INTEGER NOT NULL DEFAULT 0)"; //here the schema is to be established and create statement 
		
		try(Connection connection = SQLiteConnector.connect();
			Statement stmt = connection.createStatement()) 
		{
			if(connection != null)
			{
				stmt.execute(createStatement);
			}
			else
			{
				System.err.println("Failed to connect to database in Create table command");
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error creating table: " + e.getMessage());
		}
	}

	public static void insertUser()
	{
		//String insertStatement = "INSERT INTO users (username, rank) VALUES";
		/*string url;

		string Attributes; // will parse attributes from other interfaces

		string Checkstmt; // will be used to chekc if user is in db already 

		string InsertStatement; //here the insert statement created

		string pstmt; */ //here we will parse through the data and set the 
	}

	public static void getLeaderboard()
	{
		//List<String> leaderboard = new ArrayList<>();
		//String selectStatement = "SELECT username, rank FROM USERS ORDER BY rank DESC";

		//try (Connection connection = SQLiteConnector.connect();
			 //leaderboardQuery )
	}

	public static void UpdatePlayer()
	{
		//   string updateStatement;
	}
	


}

