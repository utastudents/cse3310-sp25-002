// -- Import Dependencies --
package uta.cse3310.PairUp;



/*
    The PairUp class will handle recieving info from the page manager system
    and relaying it to other parts of the system, this includes calls of adding players to our
    queue as well as requesting to remove a player from the queue in the case they log off
*/

 

public class PairUp {
    //
    private Matchmaking Mmaker;

  
    public PairUp() 
    {
        Mmaker = new MatchMaking();
    }


    // Add a player to the Map
    public void AddPlayer(long timeOfEntry, int playerID, String playerName, boolean playAgainstBot, int wins)
    {
        // Create a new instance of a player from the player in match making class
        PlayerInMatchmaking player = new PlayerInMatchmaking(timeOfEntry, playerID, playerName, playAgainstBot, wins);
        
        // Pass player to Matchmaking
        Mmaker.addPlayer(playerID, player);

    }
    
    public void removePlayer(int PlayerID)
    {
        // Request a removal from Matchmaking
        Mmaker.removePlayer(PlayerID);

    }

    public void pair(int p1ID, String p1Name, int p2ID, String p2Name)
    {
        // Directly establishes match between two players
        PlayerInMatchmaking p1 = new PlayerInMatchmaking(0, p1ID, p1Name, false, 0);
        PlayerInMatchmaking p2 = new PlayerInMatchmaking(0, p2ID, p2Name, false, 0);
        Mmaker.pair(p1, p2);
    }

}
