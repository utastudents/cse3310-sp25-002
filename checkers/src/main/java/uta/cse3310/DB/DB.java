package uta.cse3310.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DB {

    // Creates the USERS table with unique usernames and default rank 0
    public static void createTable() {
        String createStatement = "CREATE TABLE IF NOT EXISTS USERS (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username VARCHAR(30) NOT NULL UNIQUE,\n"
                + " rank INTEGER NOT NULL DEFAULT 0)";

        try (Connection connection = SQLiteConnector.connect();
             Statement stmt = connection.createStatement()) {
            stmt.execute(createStatement);
            insertUser("Bot1");
            insertUser("Bot2");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    // Inserts a new user and returns the auto-generated ID
    public static int insertUser(String username) {
        String insertStatement = "INSERT INTO USERS (username) VALUES(?)";
        int generatedId = -1;

        if (username == null || username.trim().isEmpty()) {
            System.err.println("Username is null or empty");
            return -1;
        }

        try (Connection connection = SQLiteConnector.connect();
             PreparedStatement pstmt = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username.trim());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1); // Get generated ID
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
        }

        return generatedId;
    }

    // Retrieves the leaderboard sorted by rank descending
    public static List<String> getLeaderboard() {
        List<String> leaderboard = new ArrayList<>();
        String selectStatement = "SELECT username, rank FROM USERS ORDER BY rank DESC";

        try (Connection connection = SQLiteConnector.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectStatement)) {

            while (rs.next()) {
                String entry = rs.getString("username") + ": " + rs.getInt("rank");
                leaderboard.add(entry);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving leaderboard: " + e.getMessage());
        }

        return leaderboard;
    }

    // Updates a user's rank based on their username
    public static boolean updatePlayer(String username, int newRank) {
        String updatePlayer = "UPDATE USERS SET rank = ? WHERE username = ?";
        boolean success = false;

        if (username == null || username.trim().isEmpty()) {
            System.err.println("Username is null or empty");
            return false;
        }

        try (Connection connection = SQLiteConnector.connect();
             PreparedStatement pstmt = connection.prepareStatement(updatePlayer)) {

            pstmt.setInt(1, newRank);
            pstmt.setString(2, username.trim());
            int rowsUpdated = pstmt.executeUpdate();
            success = rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }

        return success;
    }
}
