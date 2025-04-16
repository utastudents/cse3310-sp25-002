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

	public static void insertUser(String username)				//assumes username is already validated 
	{
		String insertStatement = "INSERT INTO users (username) VALUES(?)";	
		try(Connection connection = SQLiteConnector.connect();
			PreparedStatement pstmt = connection.prepareStatement(insertStatement)) 
		{
			if(connection != null)
			{
				pstmt.setString(1, username); 			//inserts the username into the placeholder 
				pstmt.executeUpdate();
			}
			else
			{
				System.err.println("Failed to connect to database in Insert User command");
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error inserting user: " + e.getMessage());
		}
	}

	public static List<String> getLeaderboard()
	{
		List<String> leaderboard = new ArrayList<>();
		String selectStatement = "SELECT username, rank FROM USERS ORDER BY rank DESC";

		try (Connection connection = SQLiteConnector.connect();
			 Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(selectStatement))
		{
			if (connection != null)
			{
				while(rs.next())
				{
					String entry = rs.getString("username") + ": " + rs.getInt("rank"); //here the query is parsed and entered into the List
					leaderboard.add(entry);
				}
			}
			else
			{
				System.err.println("Failed to connect to database in Leaderboard query");
			}
		}
		catch (SQLException e)
		{
			System.err.println("Error retrieving Leaderboard: " + e.getMessage());
		}
		
		return leaderboard;
	}

	public static void updatePlayer(String username, int newRank )			
	{
		String updatePlayer = "UPDATE USERS SET rank = ? WHERE username = ?";  		
		try(Connection connection = SQLiteConnector.connect();
			PreparedStatement pstmt = connection.prepareStatement(updatePlayer)) 
		{
			if(connection != null)
			{
				pstmt.setInt(1, newRank); 			//sets the newRank into the placeholder
				pstmt.setString(2, username);				
				pstmt.executeUpdate();
			}
			else
			{
				System.err.println("Failed to connect to database in Update User command");
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error updating user: " + e.getMessage());
		}

	}
}

