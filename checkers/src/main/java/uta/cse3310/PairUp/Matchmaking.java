package uta.cse3310.PairUp;

import uta.cse3310.GameManager.GamePairController;
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
    private LinkedHashMap<String, PlayerInMatchmaking> players;
    private int gameId;
    // gamePairController gameManagerCommunication;

    public Matchmaking() {
        players = new LinkedHashMap<>();
        gameId = 0;
        // gameManagerCommunication = new gamePairController;
    }

    public void pair(PlayerInMatchmaking p1, PlayerInMatchmaking p2, boolean isBotGame) {
        Random coinflip = new Random();
        boolean p1Color = coinflip.nextBoolean();
        boolean p2Color = !p1Color;
        // Match match = new Match(gameId, p1.getPlayerId, p2.getPlayerId, p1.getPlayerName, p2.getPlayerName, p1Color, p2Color, isBotGame);
        // gameManagerCommunication.newMatch(match); // Sends match info to gamePairController object for gameController to do what they want with
    }

    public void addPlayer(String PlayerID, PlayerInMatchmaking newPlayer) {
        players.put(PlayerID, newPlayer);
        matching();
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
        matching();
    }

    public void matching() {
        List<Map.Entry<String, PlayerInMatchmaking>> entries = new ArrayList<>(players.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                PlayerInMatchmaking p1 = entries.get(i).getValue();
                PlayerInMatchmaking p2 = entries.get(j).getValue();
                // TO-DO: implement matchmaking algorithm
            }
        }
    }
}
