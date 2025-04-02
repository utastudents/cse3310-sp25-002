package uta.cse3310.Bot;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.Bot.BotI.BotI;
import uta.cse3310.Bot.BotII.BotII;

public abstract class Bot {
    // The moves object is a LinkedList of Moves, which is used to store the moves
    // the bot will make.
    private Moves moves;

    // The current game board is the Board object that represents the current state
    // of the game, provided by the GameManager.
    private Board currentGameBoard;

    /**
     * 
     * 
     * Constructor for the Bot class.
     * 
     * This constructor initializes the moves object to a new instance of the Moves
     * (LinkedList of Moves), which is initially empty.
     * 
     * @param None
     */
    public Bot() {
        this.moves = new Moves();
    }

    /**
     * 
     * This method is called by the GameManager to request a move from the bot.
     * 
     * @param board - the current game {@link Board} object, sent by GameManager.
     * 
     * @return Moves - the moves object containing the bot's moves based on the
     *         current game baord
     * 
     * @see Moves
     * @see Board
     * @see GameManager
     */
    public abstract Moves requestMove(Board board);

    /**
     * 
     * This method is to be implemented by the bot specific subclasses: {@link BotI}
     * and {@link BotII} to send their constructed {@link Moves} objecct to the
     * GameManagers, called within the {@link #requestMove(Board board)} method.
     * 
     * @param None
     * 
     * @return Moves - the moves object containing the bot's move(s) based on the
     *         given game board
     * 
     * @see Moves
     * @see Board
     * 
     */
    protected abstract Moves sendMove();

    /**
     * Used to clear the moves objects after the bot has sent its moves to the
     * {@link GameManager}.
     * 
     * @param None
     * 
     * @return None
     */
    protected void flushMoves() {
        moves = new Moves();
    }

    /**
     * 
     * Gets the moves object for the bot.
     * 
     * @param None
     * 
     * @return Moves - the moves object containing the bot's move(s)
     */
    protected Moves getMoves() {
        return moves;
    }

    /**
     * 
     * Sets the moves object for the bot. Used to update the moves object after the
     * bot has decided which moves it wants to make.
     * 
     * @param moves - the moves object to be set
     * 
     * @return None
     * 
     * @see Moves
     */
    protected void setMoves(Moves moves) {
        this.moves = moves;
    }

    protected Board getCurrentGameBoard() {
        return currentGameBoard;
    }

    /**
     * 
     * Sets the current game board for the bot. This is used to update the bot's
     * knowledge of the game state, so it can make informed decisions about its
     * moves. Used within {@link #requestMove(Board board)} method.
     * 
     * @param currentGameBoard - the current game board to be set
     * 
     * @return None
     * 
     * @see Board
     */
    protected void setCurrentGameBoard(Board currentGameBoard) {
        this.currentGameBoard = currentGameBoard;
    }

}
