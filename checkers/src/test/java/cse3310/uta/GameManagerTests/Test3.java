package cse3310.uta.GameManagerTests;
import org.junit.Test;
import uta.cse3310.GameManager.GameManager;
import static org.junit.Assert.*;

public class Test3{

    @Test
    public void testActiveGameCount() {
        GameManager gm = new GameManager();

        // Initially, no active games
        assertEquals("There are no active games currently", 0, gm.getActiveGameCount());

        // Create one game
        gm.createGame(1, 2, true, false);
        assertEquals("One game created with players", 1, gm.getActiveGameCount());

        // Create another game
        gm.createGame(3, 4, true, false);
        assertEquals("Second game created with players", 2, gm.getActiveGameCount());

       // Total number of active games
        int total_no_active_game = gm.getActiveGameCount();
        assertEquals("Total active games should be 2", 2, total_no_active_game);

    }
}
