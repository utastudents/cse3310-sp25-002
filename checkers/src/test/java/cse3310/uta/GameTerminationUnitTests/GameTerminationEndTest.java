package cse3310.uta.GameTerminationUnitTests;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import uta.cse3310.GameTermination.GameTermination;

public class GameTerminationEndTest {

    @Test
    public void testEndGameWithWinner() {
        // Arrange
        GameTermination gameTermination = new GameTermination();

        // Create a mock player scores map
        Map<Integer, Integer> playerScores = new HashMap<>();
        playerScores.put(1, 10);
        playerScores.put(2, 15);

        // Act
        gameTermination.endGame(playerScores, 2); // Player 2 wins

        // Assert
        // Check that gameOver is true
        assertTrue("The game should be marked as over", gameTermination.isGameOver());
        // Check that finalWinner is Player 2
        assertEquals("Player 2", gameTermination.getWinner());
    }

    @Test
    public void testEndGameWithDraw() {
        // Arrange
        GameTermination gameTermination = new GameTermination();

        // Create a mock player scores map
        Map<Integer, Integer> playerScores = new HashMap<>();
        playerScores.put(1, 10);
        playerScores.put(2, 10);

        // Act
        gameTermination.endGame(playerScores, -1); // It's a draw

        // Assert
        // Check that gameOver is true
        assertTrue("The game should be marked as over", gameTermination.isGameOver());
        // Check that finalWinner is "Draw"
        assertEquals("Draw", gameTermination.getWinner());
    }
}
