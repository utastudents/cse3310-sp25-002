package uta.cse3310.DB;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Validate
{
    
    public static void ValidateUser(String username)
    {   
        String [] databaseUsername = new String[10];
        // DB db = new Db();
        // String databaseUsername = db.getLeaderboard();
        for (String users : databaseUsername)
        {
            if (users == username || users == null)
            {
                System.out.println("401 Invalid username");
                return;
            }
            else
            {
                System.out.println("201 Username Created");
                String insertStatement = "INSERT INTO users (username) VALUES(?)";
                String insert = insertUser(String username);
                try (java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:users.db");
                    java.sql.PreparedStatement pstmt = connection.prepareStatement(insertStatement))
                {
                    if (connection != null)
                    {
                        pstmt.setString(1, username);
                        pstmt.executeUpdate();
                    }
                    else
                    {
                        System.err.println("Failed to connect to database in Insert User command");
                    }
                }
                catch (SQLException e)
                {
                    System.err.println("Error inserting user: " + e.getMessage());
                }
                System.out.println("201 Username Created");
                return;
            }
        }
    }


}