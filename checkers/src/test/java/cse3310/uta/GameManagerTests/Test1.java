package cse3310.uta.GameManagerTests;
import org.junit.Test;
import uta.cse3310.GameManager.GameManager;
import static org.junit.Assert.*;

public class Test1 {

    @Test
    public void testCreateGameSuccessfully() {
        GameManager gm = new GameManager();
        int player1Id = 1;
        int player2Id = 2;
        boolean player1Color = true;
        boolean player2Color = false;

        boolean result = gm.createGame(player1Id, player2Id, player1Color, player2Color);

        assertTrue("Game created successfully", result);
        assertTrue("Player1 is in a game",gm.isPlayerInGame(player1Id));
        assertTrue("Player2 is in a game", gm.isPlayerInGame(player2Id));

    }
}
