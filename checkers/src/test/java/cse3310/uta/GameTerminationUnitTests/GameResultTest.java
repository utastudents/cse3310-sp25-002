package cse3310.uta.GameTerminationUnitTests;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import uta.cse3310.GameManager.Player;
import uta.cse3310.GameTermination.GameResult;

public class GameResultTest 
{
    @Test
    public void testUpdateScoresAndGetScore() 
    {
        Map<Integer, Integer> scores = new HashMap<>();
        GameResult result = new GameResult(scores);

        Player player = new Player(1, true);
        result.updateScores(player, 20);

        int actualScore = result.getScore(1);

        assertEquals(20, actualScore);
    }
}
