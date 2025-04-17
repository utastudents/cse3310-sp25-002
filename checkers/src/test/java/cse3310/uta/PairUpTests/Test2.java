package cse3310.uta.PairUpTests;

import org.junit.Test;

import uta.cse3310.Bot.BotII.Pair;
import uta.cse3310.PairUp.Match;
import uta.cse3310.PairUp.Matchmaking;
import uta.cse3310.PairUp.PlayerInMatchmaking;
import uta.cse3310.PairUp.PairUp;
import static org.junit.Assert.*;

public class Test2 
{
    /*initialize a PairUp object
    add players to matchmaking
    check if the player is in the matchmaking using assertEquals
    test for removing players:
    check if the player is removed from matchmaking using assertEquals */
    
    

    @Test
    public void TestRemovePlayer()
    {
        PairUp pairUp = new PairUp();

        pairUp.AddPlayer(151124840883L, 108, "Michael", false, 2);

        assertEquals(true, pairUp.searchPlayer(108));

        pairUp.removePlayer(108);

        assertEquals(false, pairUp.searchPlayer(108));
    }
}