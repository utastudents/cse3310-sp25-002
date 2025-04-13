package cse3310.uta.GameManagerTests;
import org.junit.jupiter.api.Test;
import uta.cse3310.GameManager.GameManager;
import static org.junit.jupiter.api.Assertions.*;

public class Test1 {

    @Test
    public void testCreateGameSuccessfully() {
        GameManager gm = new GameManager();
        int player1Id = 1;
        int player2Id = 2;
        boolean player1Color = true;
        boolean player2Color = false;

        boolean result = gm.createGame(player1Id, player2Id, player1Color, player2Color);

        assertTrue(result, "Game created successfully");
        assertTrue(gm.isPlayerInGame(player1Id), "Player1 is in a game");
        assertTrue(gm.isPlayerInGame(player2Id), "Player2 is in a game");

    }
}
