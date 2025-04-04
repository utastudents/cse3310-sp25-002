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
public class Matchmaking {
    private ArrayList<PlayerInQueue> players;
    // gamePairController gameManagerCommunication;

    public Matchmaking() {
        players = new ArrayList<>();
        // gameManagerCommunication = new gamePairController;
    }

}
