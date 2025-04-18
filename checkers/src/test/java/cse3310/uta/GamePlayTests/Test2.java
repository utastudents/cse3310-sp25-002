package cse3310.uta.GamePlayTests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GamePlay.rules;


public class Test2 extends rules 
{
    
    
    @Test 
    public void isLegal()
    {
        Game game = new Game(0,1,true,false,3);
        Move move = new Move(3,4, 4, 5);

        Game game2 = new Game(0,1,false,true,4);
        Move move2 = new Move(2,5, 3, 6);

        assertFalse(rules.isLegal(move, game)); //can't move horizontally

        assertTrue(rules.isLegal(move2, game2));// can run a normal legal move

        
        

    }
}