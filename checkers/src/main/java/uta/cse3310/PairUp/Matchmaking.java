package uta.cse3310.PairUp;

import uta.cse3310.GameManager.GamePairController;
import java.util.ArrayList;
import java.util.Random;

/*
    The Matchmaking class will deal with all things
    matchmaking. It holds all the players that are
    looking for a match. It deals with pairing them
    up, and removes them from matchmaking once they
    find a match, or if they quit matchmaking.
*/
public class Matchmaking implements Runnable {
    private ArrayList<PlayerInMatchmaking> players;
    private int gameId;
    // gamePairController gameManagerCommunication;

    public Matchmaking() {
        players = new ArrayList<>();
        gameId = 0;
        // gameManagerCommunication = new gamePairController;
        Thread thread = new Thread(this);
        thread.start();
    }

    // TO-DO: implement public void addPlayer(PlayerInMatchmaking newPlayer)

    // TO-TDO: implement public void removePlayer(String playerId)

    public void pair(PlayerInMatchmaking p1, PlayerInMatchmaking p2, boolean isBotGame) {
        Random coinflip = new Random();
        boolean p1Color = rand.nextBoolean();
        boolean p2Color = !p1Color;
        // Match match = new Match(gameId, p1.getPlayerId, p2.getPlayerId, p1.getPlayerName, p2.getPlayerName, p1Color, p2Color, isBotGame);
        // gameManagerCommunication.newMatch(match); // Sends match info to gamePairController object for gameController to do what they want with
    }

    @Override
    public void run() {
        matching();
    }

    public void matching() {
        while (true) {
            // TO-DO: implement pairing algorithm
        }
    }
}
