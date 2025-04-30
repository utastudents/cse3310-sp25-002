package cse3310.uta.PairUpTests;


import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import uta.cse3310.PairUp.Matchmaking;
import uta.cse3310.PairUp.PlayerInMatchmaking;
import uta.cse3310.GameManager.GameManager; // <-- Import GameManager
import uta.cse3310.App;
import cse3310.uta.Mock.MockApp;


import uta.cse3310.PageManager.PageManager;
class DummyPageManagerTest5 extends PageManager {
    public DummyPageManagerTest5(App app) {
        super(app);
    }
     // Add a constructor that accepts GameManager if needed, or rely on the superclass's GM
}


public class Test5 {
    // Custom PlayerInMatchmaking class for testing queue time
    private class TestPlayer extends PlayerInMatchmaking {
        private long queueTime; // Override queue time calculation

        public TestPlayer(long timeOfEntry, int playerId, String name, boolean bot, int wins) {
            // Pass actual timeOfEntry or 0 to super, manage queueTime separately for test
            super(timeOfEntry, playerId, name, bot, wins);
            this.queueTime = 0;
        }

        @Override
        public long getQueueTime() {
            // Return the explicitly set queue time for testing purposes
            return this.queueTime;
        }

        public void setQueueTime(long time) {
            this.queueTime = time;
        }
    }

    @Test
    public void TestMatching() {
        MockApp mockApp = new MockApp();
        PageManager dummyPageManagerTest5 = new DummyPageManagerTest5(mockApp);
        GameManager gameManager = new GameManager();

        Matchmaking matchmaking = new Matchmaking(dummyPageManagerTest5, gameManager);
        matchmaking.players = new LinkedHashMap<>();

        TestPlayer player1 = new TestPlayer(System.currentTimeMillis(), 1, "Player1", false, 5);
        TestPlayer player2 = new TestPlayer(System.currentTimeMillis(), 2, "Player2", false, 6);
        matchmaking.addPlayer(1, player1);
        matchmaking.addPlayer(2, player2);

        matchmaking.matching();
        assertTrue("Queue should be empty after matching similar players", matchmaking.players.isEmpty());

        matchmaking.players.clear();
        TestPlayer player3 = new TestPlayer(System.currentTimeMillis(), 3, "Player3", false, 1);
        TestPlayer player4 = new TestPlayer(System.currentTimeMillis(), 4, "Player4", false, 8);
        player3.setQueueTime(10000);
        player4.setQueueTime(10000);
        matchmaking.addPlayer(3, player3);
        matchmaking.addPlayer(4, player4);

        matchmaking.matching();
        assertEquals("Queue should have 2 players with large win difference", 2, matchmaking.players.size());

        player3.setQueueTime(70000);
        matchmaking.matching();
        assertTrue("Queue should be empty after timeout matching", matchmaking.players.isEmpty());


        matchmaking.players.clear();
        TestPlayer soloPlayer = new TestPlayer(System.currentTimeMillis(), 5, "Solo", false, 5);
        matchmaking.addPlayer(5, soloPlayer);

        matchmaking.matching();
        assertEquals("Queue should have 1 player when only one is present", 1, matchmaking.players.size());
        assertTrue("Queue should contain the solo player", matchmaking.players.containsKey(5));

        matchmaking.players.clear();
    }
}