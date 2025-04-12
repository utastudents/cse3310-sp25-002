package uta.cse3310.Bot.BotI;

import uta.cse3310.Bot.Bot;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.GameManager;

import java.util.LinkedList;

public class BotI extends Bot {

    public BotI(boolean color) {
        super(color); // Call the constructor of the parent class (Bot)
    }

    @Override
    public Moves requestMove(Board board) {
        // setCurrentGameBoard(board);
        // flushMoves();

        // LinkedList<Move> possibleMoves = determineMoves();
        // implementBotStrategy(possibleMoves);

        return null;
    }

    private LinkedList<Moves> movesLogic() {
        // This method should implement the logic to determine the moves.
        // based on the current state of the game board.
        return null; //Placeholder for now.
    }

    /* Sending Moves from Bot 1 to the GameManager */
    @Override
    protected Moves sendMove() {
        return this.moves;
    }

    private void implementStrategy(Board board, boolean isAggresive) {
        

        if (isAggresive) {
            // aggressive strategy
        }
        else {
            // passive strategy
        }
        
        
    }

}
