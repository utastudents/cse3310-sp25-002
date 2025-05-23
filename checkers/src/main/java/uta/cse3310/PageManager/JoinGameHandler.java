package uta.cse3310.PageManager;

import java.util.Map;
import uta.cse3310.PageManager.game_status;
import uta.cse3310.PairUp.PairUp;
import uta.cse3310.PairUp.PlayerInMatchmaking;

public class JoinGameHandler {

    private final PairUp pairUp;

    public JoinGameHandler(PairUp pairUp) {
        this.pairUp = pairUp;
    }

    public JoinGameHandler() {
        this(null); // debug
    }

    public Result processJoinGame(Map<String, String> joinData) {
        int clientId = Integer.parseInt(joinData.get("clientId"));  
        String gameMode = joinData.get("gameMode");

        // bot modes
        boolean isBotvBot = "BotvBot".equalsIgnoreCase(gameMode);
        boolean playAgainstBot = "Bot".equalsIgnoreCase(gameMode);
        
        if (playAgainstBot) {
            System.out.println("Bot vs Player mode selected.");
            sendToPairUpForBot(clientId);
        } else if (isBotvBot) {
            System.out.println("Bot vs Bot mode selected.");
        } else {
            System.out.println("Player vs Player mode selected.");
            sendAvailabilityToDB(clientId);
        }

        return new Result(clientId, playAgainstBot, isBotvBot); // sends data to pairup_subsys
    }

    public void cancelJoinRequest(int clientId) {
        System.out.println("Cancelling join request for clientId: " + clientId);
        if (pairUp != null) {
            pairUp.removePlayer(clientId);
        }
    }

    private void sendAvailabilityToDB(int clientId) {
        System.out.println("Flagging user as available in DB...");
        System.out.println("clientId: " + clientId);
        if (pairUp != null) {
            long timestamp = System.currentTimeMillis();
            String playerName = "Player_" + clientId;
            int wins = 0;
            pairUp.AddPlayer(timestamp, clientId, playerName, false, wins);
        }
    }

    private void sendToPairUpForBot(int clientId) {
        System.out.println("Sending request to PairUp for bot match...");
        System.out.println("clientId: " + clientId);

        if (pairUp != null) {
            long timestamp = System.currentTimeMillis();
            String playerName = "Player_" + clientId;
            int wins = 0;
            pairUp.AddPlayer(timestamp, clientId, playerName, true, wins);
        } else {
            System.err.println("PairUp instance is null in JoinGameHandler. Cannot add player for bot match.");
        }
    }

    public game_status createGameStatusMessage(int clientId, boolean playAgainstBot) {
        game_status status = new game_status();
        status.type = "join_response";
        status.msg = playAgainstBot ? "Matched with Bot!" : "Waiting for another player...";
        status.clientId = clientId;
        status.isBot = playAgainstBot;
        return status;
    }

    // Helper return class used in pairup_subsys
    public static class Result {
        public int clientId; 
        public boolean playAgainstBot;
        public boolean isBotvBot;

        public Result(int clientId, boolean playAgainstBot, boolean isBotvBot) {
            this.clientId = clientId;
            this.playAgainstBot = playAgainstBot;
            this.isBotvBot = isBotvBot;
        }
    }
}
