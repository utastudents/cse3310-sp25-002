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
                + " username varchar(30) NOT NULL UNIQUE,\n"
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
	public static void createGamesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS GAMES (\n"
                   + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                   + " player1 TEXT NOT NULL,\n"
                   + " player2 TEXT NOT NULL,\n"
                   + " winner TEXT,\n"
                   + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP\n"
                   + ");";

        try (Connection connection = SQLiteConnector.connect();
             Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating GAMES table: " + e.getMessage());
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
	     PreparedStatement pstmt = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)) {

		pstmt.setString(1, username.trim());
		pstmt.executeUpdate();

		try(ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				generatedId = generatedKeys.getInt(1);
			}
		}
	} catch (SQLException e) {
		if (e.getMessage().toLowerCase().contains("unique") || e.getMessage().toLowerCase().contains("constraint")) {
		System.err.println("Username unavailable: "+username);
		return -2; //-2 means username is already taken
		} else{
			System.err.println("Error inserting user: "+ e.getMessage());
		}
	}
	return generatedId;
	}
	public static boolean updatePlayer(String username, int newRank )			
	{
		String updatePlayer = "UPDATE USERS SET rank = ? WHERE username = ?";  	
		boolean success = false;

		if (username == null || username.trim().isEmpty()) {
			System.err.println("Username is null or empty");
			return false;
		}
		try(Connection connection = SQLiteConnector.connect();
			PreparedStatement pstmt = connection.prepareStatement("UPDATE USERS SET rank=? WHERE username = ?")) 
		{
				pstmt.setInt(1, newRank); 			//sets the newRank into the placeholder
				pstmt.setString(2, username.trim());	
				int rowsUpdated = pstmt.executeUpdate();
				success = rowsUpdated > 0;
			
		}
		catch(SQLException e)
		{
			System.err.println("Error updating user: " + e.getMessage());
		}

	return success;
}
	public static String getPlayerInfo(String username){
	String query ="SELECT id, username, rank FROM USERS WHERE username= ?";

	try(Connection connection=SQLiteConnector.connect();
	PreparedStatement pstmt = connection.prepareStatement(query)) {

		pstmt.setString(1,username.trim());
		try (ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("username");
				int rank= rs.getInt("rank");

				return "Player Info -> ID: " + id + ", Username: " + name + ", Rank:  " + rank;

			}
		}
	} catch (SQLException e) {
		System.err.println("Error fetching player info: " + e.getMessage());
	}
	return "Player not found";
}
	
 public static void recordGame(String player1, String player2, String winner) {
        String sql = "INSERT INTO GAMES (player1, player2, winner) VALUES (?, ?, ?)";

        try (Connection conn = SQLiteConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, player1);
            stmt.setString(2, player2);
            stmt.setString(3, winner); // Use "draw" if needed
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error recording game: " + e.getMessage());
        }
    }
	public static List<PlayerStat> getLeaderboard() {
    List<PlayerStat> leaderboard = new ArrayList<>();
    
    String sql = "SELECT username, " +
                 "SUM(CASE WHEN username = winner THEN 1 ELSE 0 END) AS wins, " +
                 "SUM(CASE WHEN username != winner AND winner != 'draw' THEN 1 ELSE 0 END) AS losses " +
                 "FROM ( " +
                 "  SELECT player1 AS username, winner FROM GAMES " +
                 "  UNION ALL " +
                 "  SELECT player2 AS username, winner FROM GAMES " +
                 ") " +
                 "GROUP BY username " +
                 "ORDER BY wins DESC, losses ASC";

    try (Connection connection = SQLiteConnector.connect();
         PreparedStatement stmt = connection.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
		PlayerStat stat = new PlayerStat();
            stat.username = rs.getString("username");
            stat.wins = rs.getInt("wins");
            stat.losses = rs.getInt("losses");
            leaderboard.add(stat);
        }

    } catch (SQLException e) {
        System.err.println("Error retrieving leaderboard: " + e.getMessage());
    }

    return leaderboard;
}
	public static class PlayerStat {
        public String username;
        public int wins;
        public int losses;

        @Override
        public String toString() {
            return username + ": Wins = " + wins + ", Losses = " + losses;
        }
    }
}



