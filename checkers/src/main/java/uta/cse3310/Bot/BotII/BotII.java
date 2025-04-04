package uta.cse3310.Bot.BotII;

import java.util.LinkedList;

public class BotII {

    /**
     * Implementation of requestMove method for BotII.
     * This method is called by the GameManager and BotII will return a move(s) object to the GameManager.
     * It will receive the game board and then set the appropriate field.
     */

    @Override
    public Moves requestMove(Board board) {
        return null;  // Placeholder so it compiles
    }
    

    /**
     * This function will be called to determine the all possible moves for BotII
     * and tentatively all possible routes for each move. It will use the gameboard
     * given by the game manager to determine the possible moves through the

    */
    private LinkedList<Moves> determineMoves(){
        //implementation of move determining will go here
        return null;
    }

     /**
     * Implementation of {@link Bot#sendMove()} for the BotII class. This method will
     * be called within the {@link BotII#requestMove(Board)} method to send the
     * Bot's proposed move(s) to the game manager.
     * 
     * @param None
     * 
     * @return Moves - the moves object containing the bot's move(s) based on the
     *         given game board
     * 
     * @see Bot#sendMove()
     * @see Moves
     * @see Board
     * 
     */
    @Override
    protected Moves sendMove() {
        return this.Moves;
    }

}
