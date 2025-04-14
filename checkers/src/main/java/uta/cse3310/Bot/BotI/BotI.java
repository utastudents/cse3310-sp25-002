package uta.cse3310.Bot.BotI;

import uta.cse3310.Bot.Bot;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Square;

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

        // Adding the possible moves from the commented function determineMoves(board)
        // LinkedList<Move> possibleMoves = determineMoves(board);
        // boolean isAggressive = isAggressive(board);
        // return implementStrategy(board, isAggressive, possibleMoves);

        return null;
    }

    private LinkedList<Moves> movesLogic() {
        // This method should implement the logic to determine the moves.
        // based on the current state of the game board.
        return null; // Placeholder for now.
    }

    /* Sending Moves from Bot 1 to the GameManager */
    @Override
    protected Moves sendMove() {
        return this.moves;
    }

    private void isAggressive(Board board) {
        // change void to boolean

    }

    private int countallPieces(Board board, boolean color) {

        int count = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square selectsquare = board.getSquare(row, col);

                if (selectsquare.getColor() && selectsquare.hasPiece() == color) {
                    count++;
                }
            }
        }

        return count;
    }

    private void implementStrategy(Board board, boolean isAggresive, LinkedList<Move> possibleMoves) {
        // change void to Moves
        if (isAggresive) {
            // aggressive strategy
        } else {
            // passiveStrategyImplementation(possibleMoves)
        }

    }

    /* Adding the skeleton for Passive and Aggressive Strategy */
    // private Moves passiveStrategyImplementation(LinkedList<Move> possibleMoves)
    // {
    // return null; /* Placeholder for now */
    // }

    // private Moves aggressiveStrategyImplementation(LinkedList<Move>
    // possibleMoves) {
    // return null; /* Placeholder for now */
    // }

    /**
     * determineMoves(Board board) method generates all possible legal moves for the
     * bot's pieces on the
     * board.
     * It scans each square of the board and checks if the square contains a piece
     * belonging to the bot.
     * 
     * For each piece, it checks the following:
     * 1. Normal Moves – A piece can move diagonally forward (depending on
     * color) to an empty square.
     * 2. King Moves – If the piece is a king, it can also move diagonally
     * backward to an empty square.
     * 3. Captures – A piece can jump over an opponent’s piece diagonally into
     * an empty square (forward or backward for kings).
     * 
     * The method accounts for board boundaries to avoid index out-of-bound errors.
     * All valid moves are added to a linked list and returned.
     *
     * @param board The current game board.
     * @return A LinkedList of possible moves for the bot.
     */
    private LinkedList<Move> determineMoves(Board board) {
        LinkedList<Move> validMoves = new LinkedList<>();
        // Logic Here
        return validMoves;
    }

}
