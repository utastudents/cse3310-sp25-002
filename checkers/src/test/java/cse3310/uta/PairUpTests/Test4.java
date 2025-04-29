package cse3310.uta.PairUpTests;

import org.junit.Test;
import uta.cse3310.PairUp.*;
import uta.cse3310.GameManager.Game;
import static org.junit.Assert.*;

import uta.cse3310.PageManager.PageManager;
import uta.cse3310.App;
import cse3310.uta.Mock.MockApp;


class DummyPageManagerTest4 extends PageManager {
    public DummyPageManagerTest4(App app) {
        super(app);
    }
}


public class Test4 
{
    @Test
    public void TestPairAgainstBot()
    {
        MockApp mockApp = new MockApp();

        PageManager dummyPageManagerTest4 = new DummyPageManagerTest4(mockApp);

        PlayerInMatchmaking p1 = new PlayerInMatchmaking(171328890285L,451, "Alice",true,9);

        Matchmaking matchmaking = new Matchmaking(dummyPageManagerTest4);

        // bot id = 1
        Game game = matchmaking.pair(p1, 1);

        // Assert
        assertEquals(451, game.getPlayer1ID());
        assertEquals(1, game.getPlayer2ID());

        // Check that exactly one of the players has "true" color
        boolean p1Color = game.getPlayer1Color();
        boolean p2Color = game.getPlayer2Color();
        assertNotEquals(p1Color, p2Color);
        //System.out.println("Test4 Completed"); 
    }
}