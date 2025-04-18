package cse3310.uta.GamePlayTests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
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
        
        Square blackSquare = board.getSquare(4,4);
        blackSquare.placeBlack();
        Game game = new Game(0,1,true,false,2);
        
        assertFalse(rules.canMovePiece(board, blackSquare, game));
    }
    
}
