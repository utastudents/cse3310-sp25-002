package uta.cse3310.PageManager;

import java.util.Map;

public class JoinGameHandler {

    public JoinGameHandler() {
    }

    public void processJoinGame(Map<String, String> joinData) {
        String username = joinData.get("username");
        int Client ID = joinData.get("Client ID");
        String gameMode = joinData.get("gameMode");

        if ("Multiplayer".equalsIgnoreCase(gameMode)) {
            sendAvailabilityToDB(Client ID, username);
        } else if ("Bot".equalsIgnoreCase(gameMode)) {
            sendToPairUpForBot(username, Client ID);
        }
    }

    private void sendAvailabilityToDB(int Client ID, String username) {
        System.out.println("Flagging user as available in DB...");
        System.out.println("Username: " + username + ", Client ID: " + Client ID);
    }

    private void sendToPairUpForBot(String username, int Client ID) {
        System.out.println("Sending request to PairUp for bot match...");
        System.out.println("Username: " + username + ", Client ID: " + Client ID);
    }
}
