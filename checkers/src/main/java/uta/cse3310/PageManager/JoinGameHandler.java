package uta.cse3310.PageManager;

import java.util.Map;

public class JoinGameHandler {

    public JoinGameHandler() {
    }

    public void processJoinGame(Map<String, String> joinData) {
        String ClientID = joinData.get("ClientID");
        String gameMode = joinData.get("gameMode");

        boolean isBot = "Bot".equalsIgnoreCase(gameMode);
        
        if (isBot) {
            sendToPairUpForBot(ClientID);
        } else {
            sendAvailabilityToDB(ClientID);
        }
    }

    private void sendAvailabilityToDB(String ClientID) {
        System.out.println("Flagging user as available in DB...");
        System.out.println("ClientID: " + ClientID);
    }

    private void sendToPairUpForBot(String ClientID) {
        System.out.println("Sending request to PairUp for bot match...");
        System.out.println("ClientID: " + ClientID);
    }
}
