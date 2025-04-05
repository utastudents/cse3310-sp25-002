// -- Import Dependencies --
package uta.cse3310.PairUp;
import java.util.LinkedHashMap;



/*
    The PairUp class will handle recieving info from the page manager system
    and relaying it to other parts of the system, this includes calls of adding players to our
    queue as well as requesting to remove a player from the queue in the case they log off
*/

 

public class PairUp {
    //
    private LinkedHashMap<String, PlayerInMatchmaking> MatchMap;

  
    public PairUp() 
    {
        this.MatchMap = new LinkedHashMap<>();
    }


    // Add a player to the Map
    public void AddPlayer(long timeOfEntry, String playerID, String playerName, boolean playAgainstBot, int wins)
    {
        // Create a new instance of a player from the player in match making class
        PlayerInMatchmaking player = new PlayerInMatchmaking(timeOfEntry, playerID, playerName, playAgainstBot, wins);
        
        // Add said player to LinkedHashMap
        MatchMap.put(playerID, player); 

    }

    public void removePlayer(String PlayerID)
    {

        MatchMap.remove(PlayerID);

    }

    

}
