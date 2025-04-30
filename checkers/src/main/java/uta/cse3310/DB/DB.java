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
                + " username varchar(30) NOT NULL,\n"
                + "rank INTEGER NOT NULL DEFAULT 0)"; //here the schema is to be established and create statement 
		
		try(Connection connection = SQLiteConnector.connect();
			Statement stmt = connection.createStatement()) 
		{
				stmt.execute(createStatement);
				insertUser("Bot1");
				insertUser("Bot2");
			} catch(SQLException e) {
			System.err.println("Error creating table: " + e.getMessage());
		}
	}

	public static int insertUser(String username)				//assumes username is already validated 
	{
		String insertStatement = "INSERT INTO USERS (username) Values(?)";
		int generatedId= -1;

		if (username == null || username.trim().isEmpty()) {
			System.err.println("Username is null or empty");
			return -1;
	}
	try (Connection connection = SQLiteConnector.connect();
	     PreparedStatement pstmt = connection.prepareStatement(insertStatement, Statement.RETRURN_GENERATED_KEYS)) {

		pstmt.setString(1, username.trim());
		pstmt.executeUpdate();

		try(ResulSet generateKeys = pstmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				generatedId = generatedKeys.getInt(1);
			}
		}
	} catch (SQLException e) {
		System.err.printIn("Error inserting user: "+ e.getMessage());
	}
	return generatedId;
	}

	public static List<String> getLeaderboard()
	{
		List<String> leaderboard = new ArrayList<>();
		String selectStatement = "SELECT username, rank FROM USERS ORDER BY rank DESC";

		try (Connection connection = SQLiteConnector.connect();
			 Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(selectStatement))
		{
				while(rs.next())
				{
					String entry = rs.getString("username") + ": " + rs.getInt("rank"); //here the query is parsed and entered into the List
					leaderboard.add(entry);
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
		boolean success = false;

		if (usename == null || username.trim().isEmpty()) {
			System.err.println("Username is null or empty");
			return false;
		}
		try(Connection connection = SQLiteConnector.connect();
			PreparedStatement pstmt = connection.prepareStatement(updatePlayer)) 
		{
				pstmt.setInt(1, newRank); 			//sets the newRank into the placeholder
				pstmt.setString(2, username.trim());	
				int rowsUpdated = pstmt.executeUpdate();
				success = rowsUpdateed > 0;
			
		}
		catch(SQLException e)
		{
			System.err.println("Error updating user: " + e.getMessage());
		}

	return success;
}

