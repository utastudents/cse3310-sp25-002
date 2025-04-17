package cse3310.uta.BotITests;

import uta.cse3310.Bot.BotI.BotI;
import uta.cse3310.Bot.Bot;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Square;
import java.util.LinkedList;
import static org.junit.Assert.*;
import org.junit.Test;

public class Test3 extends BotI {
    @Test
    public void isAggressiveTest() {
        Board board = new Board();

        // init white bot area & pieces
        this.setColor(true);
        board.getSquare(1, 1).placeWhite();
        board.getSquare(2, 2).placeBlack();
        board.getSquare(3, 3).placeBlack();

        boolean outcome = this.isAggressive(board);
        assertTrue(outcome);
    }
}
