
// -- Import Dependencies --
package uta.cse3310.PairUp;
import uta.cse3310.PageManager.PageManager;


/*
    The PairUp class will handle recieving info from the page manager system
    and relaying it to other parts of the system, this includes calls of adding players to our
    queue as well as requesting to remove a player from the queue in the case they log off
*/



public class PairUp {

    private final Matchmaking Mmaker;

    public PairUp(PageManager pageManager)
    {
        Mmaker = new Matchmaking(pageManager);
    }


    // Add a player to the Map
    public void AddPlayer(long timeOfEntry, int playerID, String playerName, boolean playAgainstBot, int wins)
    {
        // Create a new instance of a player from the player in match making class
        PlayerInMatchmaking player = new PlayerInMatchmaking(timeOfEntry, playerID, playerName, playAgainstBot, wins);

        // Pass player to Matchmaking
        Mmaker.addPlayer(playerID, player);

    }

    /*
        Removes a player from the matchmaking queue.
        Parameters:
        - PlayerID: The unique ID of the player to be removed
    */
    public void removePlayer(int PlayerID)
    {
        Mmaker.removePlayer(PlayerID);

    }

    /*
        Searches for a player in the matchmaking queue.
        Parameters:
        - PlayerID: The unique ID of the player to search for
        Returns:
        - Boolean indicating whether the player is in the queue
    */
    public boolean searchPlayer(int PlayerID)
    {
        return Mmaker.getPlayer(PlayerID);
    }

    /*
        Clears all players from the matchmaking queue.
        This method is typically used to reset the matchmaking system.
    */
    public void clearPlayers()
    {
        Mmaker.players.clear();
    }

    /*
        Triggers the matchmaking process.
        This method is used to attempt pairing players in the queue.
    */
    public void ping()
    {
        Mmaker.matching();
    }

    /*
        Directly pairs two players for a match.
        Parameters:
        - p1ID: The unique ID of player 1
        - p1Name: The name of player 1
        - p2ID: The unique ID of player 2
        - p2Name: The name of player 2
    */
    public void pair(int p1ID, String p1Name, int p2ID, String p2Name)
    {
        // Directly establishes match between two players
        PlayerInMatchmaking p1 = new PlayerInMatchmaking(0, p1ID, p1Name, false, 0);
        PlayerInMatchmaking p2 = new PlayerInMatchmaking(0, p2ID, p2Name, false, 0);
        Mmaker.pair(p1, p2);
    }

    // For now, creates match between two random bots
    public void createBotGame(int creatorID)
    {
        int bot1ID = (int) (Math.random() * 2);
        int bot2ID = (int) (Math.random() * 2);
        Mmaker.pair(bot1ID, bot2ID, creatorID);
    }

}
