package cse3310.uta.GameManagerTests;
import org.junit.Test;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.Moves;

import static org.junit.Assert.*;

public class Test2 {

    @Test
    public void movetest() {
        
        Board board = new Board();
        board.initializeBoard();
        System.out.print(board.toString());
        Moves p1moves = new Moves();
        Moves p2moves = new Moves();

        p1moves.addNext(p1moves.makeMove(board, board.getSquare(2, 1), false, false, false));
        board.execute(p1moves.getFirst(), false);
        //visual test of move performed
        System.out.print(board.toString());
        assertTrue("this square should not have a piece anymore", board.getSquare(2,1).hasPiece() == false);
        assertTrue("this square should have a piece now", board.getSquare(3,2).hasPiece() == true);
        p2moves.addNext(p2moves.makeMove(board, board.getSquare(5, 2), true, true, false));
        board.execute(p2moves.getFirst(), false);
        //visual test of move performed
        System.out.print(board.toString());
        assertTrue("this square should not have a piece anymore", board.getSquare(5,2).hasPiece() == false);
        assertTrue("this square should have a piece now", board.getSquare(4,3).hasPiece() == true);
    }
}
