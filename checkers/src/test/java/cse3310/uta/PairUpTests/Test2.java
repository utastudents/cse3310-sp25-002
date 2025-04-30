package cse3310.uta.PairUpTests;

import org.junit.Test;

import uta.cse3310.PairUp.PairUp;
import uta.cse3310.GameManager.GameManager;
import static org.junit.Assert.*;
import uta.cse3310.PageManager.PageManager;
import uta.cse3310.App;
import cse3310.uta.Mock.MockApp;


class DummyPageManagerTest2 extends PageManager {
    public DummyPageManagerTest2(App app) {
        super(app);
    }
}


public class Test2
{
    @Test
    public void TestRemovePlayer()
    {
        MockApp mockApp = new MockApp();
        PageManager dummyPageManagerTest2 = new DummyPageManagerTest2(mockApp);
        GameManager gameManager = new GameManager();

        PairUp pairUp = new PairUp(dummyPageManagerTest2, gameManager);

        pairUp.AddPlayer(151124840883L, 108, "Michael", false, 2);

        assertTrue("Player should be searchable after adding", pairUp.searchPlayer(108));

        pairUp.removePlayer(108);

        assertFalse("Player should not be searchable after removing", pairUp.searchPlayer(108));
    }
}