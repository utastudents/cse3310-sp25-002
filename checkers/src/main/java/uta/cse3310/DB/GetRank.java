package uta.cse3310.DB;
public class GetRank
{
    public static void getRank(int playerId)
    {
        // This method will be used to get the rank of a player from the database.
        // It will take the player's ID as input and return their rank.
        // DB db = new DB();
        String [] UserData = new String[10];
        int rank = 1;
        for (String users : UserData)
        {
            if (Integer.parseInt(users) == playerId)
            {
                System.out.println("Rank: " + rank);
                break;
                
            }else{
                rank ++;
            }
        }
        // Placeholder for actual database logic

    }
    public static void updateScore(String username, String Oppusername)
    {
        // This will update and store score of the player in the database.
        String query = "SELECT username, rank FROM USERS WHERE username IN (?,?)";
    }
    // public List<String> getLeaderboard()
    // {
    //     // This method will be used to get the leaderboard from the database.
    //     // It will return a list of players and their ranks.
    //     return List.of("user1", "user2", "user3"); // Placeholder for actual database logic
    // }
}
