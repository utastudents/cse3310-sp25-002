package cse3310.uta.PairUpTests;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import uta.cse3310.PairUp.Matchmaking;
import uta.cse3310.PairUp.PlayerInMatchmaking;

public class Test5 {
    // Custom PlayerInMatchmaking class for testing
    private class TestPlayer extends PlayerInMatchmaking {
        private long queueTime;

        public TestPlayer(long userId, int playerId, String name, boolean bot, int wins) {
            super(userId, playerId, name, bot, wins);
            this.queueTime = 0;
        }

        @Override
        public long getQueueTime() {
            return this.queueTime;
        }

        public void setQueueTime(long time) {
            this.queueTime = time;
        }
    }

    @Test
    public void TestMatching() {
        Matchmaking matchmaking = new Matchmaking();
        matchmaking.players = new LinkedHashMap<>();

        // Test 1: Players with similar wins should match
        TestPlayer player1 = new TestPlayer(1L, 1, "Player1", false, 5);
        TestPlayer player2 = new TestPlayer(2L, 2, "Player2", false, 6);
        matchmaking.players.put(1, player1);
        matchmaking.players.put(2, player2);
        
        matchmaking.matching();
        // Similar win players should be matched and removed
        assertTrue(matchmaking.players.isEmpty());

        // Test 2: Players with large win difference should not match initially
        TestPlayer player3 = new TestPlayer(3L, 3, "Player3", false, 1);
        TestPlayer player4 = new TestPlayer(4L, 4, "Player4", false, 8);
        player3.setQueueTime(0); // Set queue time to less than 60 seconds
        player4.setQueueTime(0);
        matchmaking.players.put(3, player3);
        matchmaking.players.put(4, player4);
        /*System.out.println("p3 q time:" + player3.getQueueTime());
        System.out.println("p4 q time:" + player4.getQueueTime());
        System.out.println("Before:");
        for (Map.Entry<Integer, PlayerInMatchmaking> entry : matchmaking.players.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }*/

        matchmaking.matching();
        /*System.out.println("After:");
        for (Map.Entry<Integer, PlayerInMatchmaking> entry : matchmaking.players.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }*/
        // Players with large win difference should stay in queue
        assertEquals(2, matchmaking.players.size());

        // Test 3: Single player should remain in queue
        matchmaking.players.clear();
        TestPlayer soloPlayer = new TestPlayer(5L, 5, "Solo", false, 5);
        matchmaking.players.put(5, soloPlayer);
        
        matchmaking.matching();
        // Single player should remain in queue
        assertEquals(1, matchmaking.players.size());
        // Queue should contain the solo player
        assertTrue(matchmaking.players.containsKey(5));
    }
}