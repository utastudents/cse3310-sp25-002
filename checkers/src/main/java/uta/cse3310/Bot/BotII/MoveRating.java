package uta.cse3310.Bot.BotII;

import uta.cse3310.GameManager.Move;

public class MoveRating {

    private Move move;
    private int eloRating;

    public MoveRating(Move move, int eloRating) {
        this.move = move;
        this.eloRating = eloRating;
    }

    public Move getMove() {
        return move;
    }

    public int getEloRating() {
        return eloRating;
    }

}
