package cse3310.uta.GamePlayTests;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GamePlay.rules;


public class Test1 extends rules
{


    @Test
    public void canMovePieceTest()
    {
        Board board = new Board();
        board.initializeBoard();

        Game game = new Game(0, 1, false, true, 2);

        Square startSquare = board.getSquare(2, 1);
        Square destSquare = board.getSquare(3, 0);

        Move testMove = new Move(startSquare, destSquare);

        assertTrue("Black should be able to make the opening move 2,1 -> 3,0", rules.canMovePiece(game, testMove));
    }

}