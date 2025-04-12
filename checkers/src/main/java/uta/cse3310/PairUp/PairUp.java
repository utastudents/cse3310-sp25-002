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
        
    }


    // Add a player to the Map
    public void AddPlayer(long timeOfEntry, String playerID, String playerName, boolean playAgainstBot, int wins)
    {
        // Create a new instance of a player from the player in match making class
        PlayerInMatchmaking player = new PlayerInMatchmaking(timeOfEntry, playerID, playerName, playAgainstBot, wins);
        
        // Pass player to Matchmaking
        Mmaker.addPlayer(playerID, player);

    }

    
    public void removePlayer(String PlayerID)
    {
        // Request a removal from Matchmaking
        Mmaker.removePlayer(PlayerID);

    }

    

}
