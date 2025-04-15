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
    private LinkedHashMap<Integer, PlayerInMatchmaking> players;
    private int gameId;
    GamePairController gameManagerCommunication;

    public Matchmaking() {
        players = new LinkedHashMap<>();
        gameId = 0;
        gameManagerCommunication = new GamePairController();
    }

    public void pair(PlayerInMatchmaking p1, PlayerInMatchmaking p2, boolean isBotGame) {
        Random coinflip = new Random();
        boolean p1Color = coinflip.nextBoolean();
        boolean p2Color = !p1Color;
        Match match = new Match(p1.getPlayerID(), p2.getPlayerID(), p1.getPlayerName(), p2.getPlayerName(), isBotGame, gameId++, p1Color, p2Color);
        gameManagerCommunication.newMatch(match); // Sends match info to gamePairController object for gameController to do what they want with
    }

    public void addPlayer(int PlayerID, PlayerInMatchmaking newPlayer) {
        players.put(PlayerID, newPlayer);
        matching();
    }

    public void removePlayer(int playerId) {
        players.remove(playerId);
        matching();
    }

    public void matching() {
        List<Map.Entry<Integer, PlayerInMatchmaking>> entries = new ArrayList<>(players.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                PlayerInMatchmaking p1 = entries.get(i).getValue();
                PlayerInMatchmaking p2 = entries.get(j).getValue();
                // TO-DO: implement matchmaking algorithm

                // Put this line into when a match is made between two humans
                pair(p1, p2, false);
            }
        }
    }
}
