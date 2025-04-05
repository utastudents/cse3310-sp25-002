package uta.cse3310.Bot.BotII;

import uta.cse3310.Bot.Bot;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.Moves;

import java.util.LinkedList;

public class BotII extends Bot {
    
    /**
     * Constructor for the BotII class. This constructor initializes the moves
     * object to a new instance of the Moves (LinkedList of Moves), which is
     * initially empty.
     * 
     * @param color - the color the bot will be playing as (true for black, false
     *              for white)
     */
      public BotII(boolean color) {
       super(color); // Call the constructor of the parent class (Bot)
     }
     

    /**
     * Implementation of requestMove method for BotII.
     * This method is called by the GameManager and BotII will return a move(s)
     * object to the GameManager.
     * It will receive the game board and then set the appropriate field.
     * 
     * @param board - the current game {@link Board} object, sent by GameManager.
     * 
     * @return Moves - the moves object containing the bot's moves based on the
     *         current game board
     * 
     * @see Moves
     * @see Board
     * @see GameManager
     * @see Bot
     */
    // @Override, commented this out until botI writes theirs so system can compile for now
    // It is an abstract method in the parent class Bot, so it must be implemented here, but can't be until BotI is done
    public Moves requestMove(Board board) {
        return null; // Placeholder so it compiles
    }

    /**
     * This function will be called to determine the all possible moves for BotII
     * and tentatively all possible routes for each move. It will use the gameboard
     * given by the game manager to determine the possible moves through the
     * {@link BotII#determineMoves()} function.
     * 
     * @param none
     * 
     * @return A list of all possible {@link Moves} for BotII.
     * 
     * @see BotII#determineMoves()
     * @see Board
     * @see Moves
     */
    private LinkedList<Moves> determineMoves() {
        // implementation of move determining will go here
        return null;
    }
    
    /**
     * Implements BotII's strategy (predetermined) based on the moves it can make,
     * as determined by {@link BotII#determineMoves()}. Once determined, this method
     * will set the moves object to the list of moves that BotII will make.
     *
     * @param none
     *
     * @return Nothing – the moves object is set to the list of moves that BotII
     *         will make
     *
     * @see BotII#determineMoves()
     * @see Moves
     * @see Board
     */
    private void implementBotStrategy() {

    }
    
    /**
     * Implementation of {@link Bot#sendMove()} for the BotII class. This method
     * will
     * be called within the {@link BotII#requestMove(Board)} method to send the
     * Bot's proposed move(s) to the game manager.
     * 
     * @param None
     * 
     * @return Moves - the moves object containing the bot's move(s) based on the
     *         given game board
     * 
     * @see Bot
     * @see Moves
     * @see Board
     * 
     */
    @Override
    protected Moves sendMove() {
        return this.moves;
    }


}
