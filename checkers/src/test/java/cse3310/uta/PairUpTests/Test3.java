package cse3310.uta.PairUpTests;
import org.junit.Test;
import uta.cse3310.PairUp.*;
import uta.cse3310.GameManager.GameManager;
import static org.junit.Assert.*;
import cse3310.uta.Mock.MockApp;
import uta.cse3310.App;
import uta.cse3310.PageManager.PageManager;


class DummyPageManagerTest3 extends PageManager {
    public DummyPageManagerTest3(App app) {
        super(app);
    }
}

public class Test3
{
    @Test
    public void TestAddPlayer()
    {
        MockApp mockApp = new MockApp();
        PageManager dummyPageManagerTest3 = new DummyPageManagerTest3(mockApp);
        GameManager gameManager = new GameManager();

        PairUp pairUp = new PairUp(dummyPageManagerTest3, gameManager);

        pairUp.AddPlayer(171328890285L,451, "Alice",false,9);

        assertTrue("Player should be searchable after adding", pairUp.searchPlayer(451));

        pairUp.clearPlayers();
        assertFalse("Player should not be searchable after clearing", pairUp.searchPlayer(451));

    }
}