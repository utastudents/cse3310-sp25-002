package cse3310.uta.GameManagerTests;
import org.junit.Test;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.Game;
import uta.cse3310.PairUp.Match;

import static org.junit.Assert.*;

public class Test1 {

    @Test
    public void testCreateGameSuccessfully() {
        GameManager gm = new GameManager();
        int player1Id = 2;
        int player2Id = 3;
        boolean player1Color = true;
        boolean player2Color = false;
        String player1Name = "LeGOAT";
        String player2Name = "James";
        int gameID = 7;
        Match match = new Match(player1Id, player2Id, player1Name, player2Name, false, gameID, player1Color, player2Color);
        Game newGame = gm.createGame(match);
        boolean result = false;
        if (newGame != null) {
            result = true;
        }

        assertTrue("Game created successfully", result);
        assertTrue("Player1 is in a game",gm.isPlayerInGame(player1Id));
        assertTrue("Player2 is in a game", gm.isPlayerInGame(player2Id));

    }
}
