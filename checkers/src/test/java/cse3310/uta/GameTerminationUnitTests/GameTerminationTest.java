package cse3310.uta.GameTerminationUnitTests;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.DB.DB;



public class GameTerminationTest {

    @Test
    public void testGetPlayerHasLegalMoves() {
        GameTermination termination = new GameTermination();
        Map<Integer, Boolean> legalMoves = termination.getPlayerHasLegalMoves();

        assertEquals(2, legalMoves.size());
        assertTrue(legalMoves.containsKey(1));
        assertTrue(legalMoves.containsKey(2));
        assertFalse(legalMoves.get(1));  // Player 1 has no legal moves
        assertTrue(legalMoves.get(2));   // Player 2 has legal moves
    }


    @Test
    public void testSaveResultsToDatabase() {
        GameTermination termination = new GameTermination();

        Map<Integer, Integer> scores = new HashMap<>();
        scores.put(1, 12);  
        scores.put(2, 18);  
        DB.insertUser("Player1");
        DB.insertUser("Player2");
        termination.saveResultsToDatabase(scores);

        List<String> leaderboard = DB.getLeaderboard();

        boolean foundPlayer1 = false; 
        boolean foundPlayer2 = false;
        for (String entry : leaderboard) {
            if (entry.startsWith("Player1:")) {
                assertTrue(entry.contains("12"));
                foundPlayer1 = true;
            }
            if (entry.startsWith("Player2:")) {
                assertTrue(entry.contains("18"));
                foundPlayer2 = true;
            }
        }
        assertTrue("Cannot find player1 in leaderboard", foundPlayer1);
        assertTrue("Cannot find player2 in leaderboard", foundPlayer2);
    }
}
