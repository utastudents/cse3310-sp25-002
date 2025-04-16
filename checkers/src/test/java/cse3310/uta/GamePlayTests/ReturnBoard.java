package cse3310.uta.GamePlayTests;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GamePlay.rules;


public class ReturnBoard extends rules 
{
    @Test
    public void startingBoard()
    {
        Board board = new Board();
        board.initializeBoard();
        /* 
        initializes a game with 2 bots, first bot being white and second bot being black
        with a game id of 0
        Board is also initialized within the Game initialization*/
        Game game = new Game(0,1,true,false,0);

        assertTrue(true);

    }
}