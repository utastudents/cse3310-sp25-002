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

public class Test1 extends BotI {
    @Test
    public void testingPlayCapture() {
        Board board = new Board();
        this.setColor(true);

        Square fromPosition = board.getSquare(2, 2);
        fromPosition.placeWhite(); // Bot piece (white)

        Square midPosition = board.getSquare(3, 3);
        midPosition.placeBlack(); // Opponent's piece

        Square toPosition = board.getSquare(4, 4);
        LinkedList<Move> moves = new LinkedList<>();

        this.playCapture(moves, fromPosition, 4, 4, 3, 3, board);

        assertTrue(moves.size() == 1); // One Move added check
        assertTrue(moves.get(0).getStart() == fromPosition); // check start position
        assertTrue(moves.get(0).getDest() == toPosition); // check the jump position if middle piece is opponent
    }

}
