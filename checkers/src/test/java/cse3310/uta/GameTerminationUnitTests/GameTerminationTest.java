package cse3310.uta.GameTerminationUnitTests;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

import uta.cse3310.GameTermination.GameTermination;

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
}
