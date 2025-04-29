package cse3310.uta.PairUpTests;

import org.junit.Test;

import uta.cse3310.PairUp.PairUp;
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
    /*initialize a PairUp object
    add players to matchmaking
    check if the player is in the matchmaking using assertEquals
    test for removing players:
    check if the player is removed from matchmaking using assertEquals */
    
    

    @Test
    public void TestRemovePlayer()
    {
        MockApp mockApp = new MockApp();
        PageManager dummyPageManagerTest2 = new DummyPageManagerTest2(mockApp);
        PairUp pairUp = new PairUp(dummyPageManagerTest2);

        pairUp.AddPlayer(151124840883L, 108, "Michael", false, 2);

        assertEquals(true, pairUp.searchPlayer(108));

        pairUp.removePlayer(108);

        assertEquals(false, pairUp.searchPlayer(108));
    }
}