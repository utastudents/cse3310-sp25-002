package cse3310.uta.PairUpTests;
import org.junit.Test;
import uta.cse3310.PairUp.*;
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
        PairUp pairUp = new PairUp(dummyPageManagerTest3);

        pairUp.AddPlayer(171328890285L,451, "Alice",false,9);
    
        assertEquals(true, pairUp.searchPlayer(451));
        
        pairUp.clearPlayers();

    }
}