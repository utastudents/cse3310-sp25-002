package uta.cse3310.PairUp;

import uta.cse3310.GameManager.GamePairController;
import uta.cse3310.GameManager.Game;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.*;

/*
    The Matchmaking class will deal with all things
    matchmaking. It holds all the players that are
    looking for a match. It deals with pairing them
    up, and removes them from matchmaking once they
    find a match, or if they quit matchmaking.
*/
public class Matchmaking {
    private LinkedHashMap<Integer, PlayerInMatchmaking> players;
    private int gameId;
    GamePairController gameManagerCommunication;

    public Matchmaking() {
        players = new LinkedHashMap<>();
        gameId = 0;
        gameManagerCommunication = new GamePairController();
    }

    // Pairs two players
    public Game pair(PlayerInMatchmaking p1, PlayerInMatchmaking p2) {
        Random coinflip = new Random();
        boolean p1Color = coinflip.nextBoolean();
        boolean p2Color = !p1Color;
        Match match = new Match(p1.getPlayerID(), p2.getPlayerID(), p1.getPlayerName(), p2.getPlayerName(), false, gameId++, p1Color, p2Color);
        return gameManagerCommunication.newMatch(match); // Sends match info to gamePairController object for gameController to do what they want with
    }

    // Pairs a plyer and bot
    public Game pair(PlayerInMatchmaking p1, int botID) {
        Random coinflip = new Random();
        boolean p1Color = coinflip.nextBoolean();
        boolean botColor = !p1Color;
        Match match = new Match(p1.getPlayerID(), botID, p1.getPlayerName(), "Bot", true, gameId++, p1Color, botColor);
        return gameManagerCommunication.newMatch(match); // Sends match info to gamePairController object for gameController to do what they want with
    }

    public void addPlayer(int PlayerID, PlayerInMatchmaking newPlayer) {
        if (newPlayer.isPlayAgainstBot()) {
            int botID = (int) (Math.random() * 2);
            pair(newPlayer, botID);
        }
        else {
            players.put(PlayerID, newPlayer);
            matching();
        }
    }

    public void removePlayer(int playerId) {
        players.remove(playerId);
        matching();
    }

    public void matching() {
    List<Map.Entry<Integer, PlayerInMatchmaking>> entries = new ArrayList<>(players.entrySet());
    for (int i = 0; i < entries.size(); i++) {
        PlayerInMatchmaking p1 = entries.get(i).getValue();
        
        // Skip if player has already been matched
        if (!players.containsKey(entries.get(i).getKey())) {
            continue;
        }

        boolean matched = false;
        
        // First try to match with players within +/- 1 win
        for (int j = i + 1; j < entries.size() && !matched; j++) {
            PlayerInMatchmaking p2 = entries.get(j).getValue();
            
            // Skip if player has already been matched
            if (!players.containsKey(entries.get(j).getKey())) {
                continue;
            }

            int winDifference = Math.abs(p1.getWins() - p2.getWins());
 
            if (winDifference <= 1) {
                pair(p1, p2);
                players.remove(p1.getPlayerID());
                players.remove(p2.getPlayerID());
                matched = true;
            }
        }

        // If no match found and player has been waiting > 60 seconds, match with next available player
        if (!matched && p1.getQueueTime() > 60000) { // 60000 milliseconds = 60 seconds
            for (int j = i + 1; j < entries.size() && !matched; j++) {
                PlayerInMatchmaking p2 = entries.get(j).getValue();
                
                
                if (!players.containsKey(entries.get(j).getKey())) {
                    continue;
                }

                pair(p1, p2);
                players.remove(p1.getPlayerID());
                players.remove(p2.getPlayerID());
                matched = true;
             }
            }
        
       
}
}
}