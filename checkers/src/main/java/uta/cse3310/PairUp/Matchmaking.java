package uta.cse3310.PairUp;

import uta.cse3310.GameManager.GamePairController;
import java.util.ArrayList;

/*
    The Matchmaking class will deal with all things
    matchmaking. It holds all the players that are
    looking for a match. It deals with pairing them
    up, and removes them from matchmaking once they
    find a match, or if they quit matchmaking.
*/
public class Matchmaking implements Runnable {
    private ArrayList<PlayerInMatchmaking> players;
    // gamePairController gameManagerCommunication;

    public Matchmaking() {
        players = new ArrayList<>();
        // gameManagerCommunication = new gamePairController;
        Thread thread = new Thread(this)
        thread.start();
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
