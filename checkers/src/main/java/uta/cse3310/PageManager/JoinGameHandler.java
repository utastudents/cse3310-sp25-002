package uta.cse3310.PageManager;

import java.util.Map;

public class JoinGameHandler {
    
    public JoinGameHandler() {
    }

    public Result processJoinGame(Map<String, String> joinData) {
        int clientID = Integer.parseInt(joinData.get("ClientID"));  
        String gameMode = joinData.get("gameMode");
        boolean playAgainstBot = "Bot".equalsIgnoreCase(gameMode);
        
        if (playAgainstBot) {
            sendToPairUpForBot(clientID);
        } else {
            sendAvailabilityToDB(clientID);
        }

        return new Result(clientID, playAgainstBot); // sends data to pairup_subsys
    }

    public void cancelJoinRequest(int clientID) {
        System.out.println("Cancelling join request for ClientID: " + clientID);
    }

    private void sendAvailabilityToDB(int clientID) {
        System.out.println("Flagging user as available in DB...");
        System.out.println("ClientID: " + clientID);
    }

    private void sendToPairUpForBot(int clientID) {
        System.out.println("Sending request to PairUp for bot match...");
        System.out.println("ClientID: " + clientID);
    }

    // Feedback structure to send back to PageManager/frontend
    public static class game_status {
        public String type;
        public String msg;
    }

    public game_status createGameStatusMessage(int clientID, boolean playAgainstBot) {
        game_status status = new game_status();
        status.type = "join_response";
        status.msg = playAgainstBot ? "Matched with Bot!" : "Waiting for another player...";
        return status;
    }

    // Helper return class used in pairup_subsys
    public static class Result {
        public int clientID; 
        public boolean playAgainstBot;

        public Result(int clientID, boolean playAgainstBot) {
            this.clientID = clientID;
            this.playAgainstBot = playAgainstBot;
        }
    }
}
