package uta.cse3310.Bot.BotI;

import uta.cse3310.Bot.Bot;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;

import java.util.LinkedList;

public class BotI extends Bot {

    @Override
    public Moves requestMove(Board board){
        setCurrentGameBoard(board);
        flushMoves();

        LinkedList<Move> possibleMoves = determineMoves();
        implementBotStrategy(possibleMoves);

        return null//sendMove();
    }


    /*waiting to complete to uncomment
    public BotI(boolean color){
        super(color); // Call the constructor of the parent class (Bot)
    }
    */
   
    /* Sending Moves from Bot 1 to the GameManager */
    @Override
    protected Moves sendMove() {
        return this.moves;
    }

    private void implementStrategy() {
        //add strategy here
    }

}
