package uta.cse3310.DB;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
public class GetRank
{
    
    public int getRank(String userId)
    {
        // This method will be used to get the rank of a player from the database.
        // It will take the player's ID as input and return their rank.
        // DB db = new DB();
        String [] UserData = new String[10];
        int rank = 1;
        for (String users : UserData)
        {
            if (users == userId)
            {
                return rank;
                
            }else{
                rank ++;
            }
        }
        return rank;

    }
    public static void updateScore()
    {
        // This will update and store score of the player in the database.
    }
    // public List<String> getLeaderboard()
    // {
    //     // This method will be used to get the leaderboard from the database.
    //     // It will return a list of players and their ranks.
    //     return List.of("user1", "user2", "user3"); // Placeholder for actual database logic
    // }
}
