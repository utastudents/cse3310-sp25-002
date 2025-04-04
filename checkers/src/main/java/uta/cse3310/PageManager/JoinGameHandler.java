package uta.cse3310.PageManager;

import java.util.Map;

public class JoinGameHandler {

    public JoinGameHandler() {
    }

    public void processJoinGame(Map<String, String> joinData) {
        String username = joinData.get("username");
        String userID = joinData.get("userID");
        String gameMode = joinData.get("gameMode");

        if ("Multiplayer".equalsIgnoreCase(gameMode)) {
            sendAvailabilityToDB(userID, username);
        } else if ("Bot".equalsIgnoreCase(gameMode)) {
            sendToPairUpForBot(username, userID);
        }
    }

    private void sendAvailabilityToDB(String userID, String username) {
        System.out.println("Flagging user as available in DB...");
        System.out.println("Username: " + username + ", UserID: " + userID);
    }

    private void sendToPairUpForBot(String username, String userID) {
        System.out.println("Sending request to PairUp for bot match...");
        System.out.println("Username: " + username + ", UserID: " + userID);
    }
}
