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

public class Test2 extends BotI {
    /* Testing playNormalMove */
    @Test
    public void testingPlayNormalMove() {
        Board board = new Board();
        this.setColor(true);

        Square fromPosition = board.getSquare(1, 1);
        fromPosition.placeWhite();
        LinkedList<Move> moves = new LinkedList<>();
        this.playNormalMove(moves, fromPosition, 2, 2, board);

        assertTrue(moves.size() == 1); // One Move added check
        assertTrue(moves.get(0).getStart() == fromPosition); // check start position
        assertTrue(moves.get(0).getDest() == board.getSquare(2, 2)); // check the jump position
    }
}