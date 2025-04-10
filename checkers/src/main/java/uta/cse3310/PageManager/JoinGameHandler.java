package uta.cse3310.PageManager;

import java.util.Map;

public class JoinGameHandler {

    public JoinGameHandler() {
    }

    public void processJoinGame(Map<String, String> joinData) {
        String username = joinData.get("username");
        int ClientID = joinData.get("ClientID");
        String gameMode = joinData.get("gameMode");

        if ("Multiplayer".equalsIgnoreCase(gameMode)) {
            sendAvailabilityToDB(ClientID, username);
        } else if ("Bot".equalsIgnoreCase(gameMode)) {
            sendToPairUpForBot(username, ClientID);
        }
    }

    private void sendAvailabilityToDB(int ClientID, String username) {
        System.out.println("Flagging user as available in DB...");
        System.out.println("Username: " + username + ", ClientID: " + ClientID);
    }

    private void sendToPairUpForBot(String username, int ClientID) {
        System.out.println("Sending request to PairUp for bot match...");
        System.out.println("Username: " + username + ", ClientID: " + ClientID);
    }
}
