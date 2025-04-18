package uta.cse3310.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetRank
{
    public static int getRank(String playerName)
    {
        // This method will be used to get the rank of a player from the database.
        // It will take the player's ID as input and return their rank.
        // DB db = new DB();
        // String writeQuery = "SELECT rank FROM USERS WHERE username = ?";

        // String [] UserData = new String[10];
        // int rank = 1;
        // for (String users : UserData)
        // {
        //     if (Integer.parseInt(users) == playerId)
        //     {
        //         System.out.println("Rank: " + rank);
        //         break;
                
        //     }else{
        //         rank ++;
        //     }
        // }
        // Placeholder for actual database logic
        String writeQuery = "SELECT rank FROM USERS WHERE username = ?";
        try (Connection conn = SQLiteConnector.connect();
        PreparedStatement conPreparedStatement = conn.prepareStatement(writeQuery)) {

        conPreparedStatement.setString(1, playerName);
        ResultSet resultSet = conPreparedStatement.executeQuery();

       if (resultSet.next()) {
           return resultSet.getInt("rank");
       } else {
           System.out.println("User not found.");
           return -1;
       }

   } catch (SQLException e) {
       System.err.println("Error retrieving stored rank: " + e.getMessage());
       return -1;
   }

}
    public static int updateScore(String playerUsername, String oppUsername, double W)
    {
        // This query gets username and rank of the player and his opp
        String getRanks = "SELECT username, rank FROM USERS WHERE username IN (?,?)";

        try( Connection connection = SQLiteConnector.connect();
             PreparedStatement preppedStatement = connection.prepareStatement(getRanks))
        {
            // Replaces the ?s in the query with the passed in usernames
            preppedStatement.setString(1,playerUsername);
            preppedStatement.setString(2,oppUsername);

            // run query and store the result in resultSet
            ResultSet resultSet = preppedStatement.executeQuery();

            // declare ints for player's rank and the rank for their opp too
            // set them both to -1 to error check later
            int playerRank = -1;
            int oppRank = -1;

            // run through each row and assign the correct rank to the player and their opp
            while(resultSet.next())
            {
                String currUsername = resultSet.getString("username");
                int currRank = resultSet.getInt("rank");

                if (currUsername.equals(playerUsername)) 
                {
                    playerRank = currRank;
                }
                else if (currUsername.equals(oppUsername))
                {
                    oppRank = currRank;
                }
            }

            // error check in case one of the users was not found.
            if(playerRank == -1 || oppRank == -1)
            {
                System.err.println("ERROR: Could not find one or both users!");
                return -1;
            }

            // This next part is where the rank is calculated using the 
            // formula in the requirements document.
            int rankDiff = oppRank - playerRank;
            double We = 1.0 / (Math.pow(10, - rankDiff / 400.0) + 1);
            int K;
            if(playerRank < 2100)
            {
                K = 32;
            }
            else if(playerRank < 2400)
            {
                K = 24;
            }
            else
            {
                K = 16;
            }
            int newRank = (int) Math.round(playerRank + K * (W - We));

            return newRank;
        } catch(SQLException e) {
            System.err.println("Error updating score: " + e.getMessage());
            return -1;
        }

    }

}
