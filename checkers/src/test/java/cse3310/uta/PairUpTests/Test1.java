package cse3310.uta.PairUpTests;

import org.junit.Test;
import uta.cse3310.PairUp.*;
import uta.cse3310.GameManager.Game;
import static org.junit.Assert.*;
import uta.cse3310.PageManager.PageManager;
import uta.cse3310.App;
import cse3310.uta.Mock.MockApp;

class DummyPageManagerTest1 extends PageManager {
    public DummyPageManagerTest1(App app) {
        super(app);
    }
}

public class Test1 
{
    @Test
    public void TestPairWithPlayers()
    {
        MockApp mockApp = new MockApp();
        PageManager dummyPageManagerTest1 = new DummyPageManagerTest1(mockApp);
        PlayerInMatchmaking p1 = new PlayerInMatchmaking(171328890285L,451, "Alice",false,9);
        PlayerInMatchmaking p2 = new PlayerInMatchmaking(171328890789L,452, "Bob",false,10);

        Matchmaking matchmaking = new Matchmaking(dummyPageManagerTest1);

        // Act
        Game game = matchmaking.pair(p1, p2);  // now it returns the Game object

        // Assert
        assertEquals(451, game.getPlayer1ID());
        assertEquals(452, game.getPlayer2ID());

        // Check that exactly one of the players has "true" color
        boolean p1Color = game.getPlayer1Color();
        boolean p2Color = game.getPlayer2Color();
        assertNotEquals(p1Color, p2Color); 
    }
}