package uta.cse3310.Bot.BotII;

import uta.cse3310.Bot.Bot;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;

import java.util.Collections;
import java.util.Comparator;
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
     * Handles the first move logic for BotII.
     * It checks all pieces in the front row and tries to move them diagonally if
     * possible.
     *
     * @param board the current game board
     * @return a single randomly chosen move if valid ones exist, null otherwise
     */
    private Moves startMove(Board board) {
        Moves firstMoveSet = new Moves(); // Collect valid first moves

        int frontRow = this.color ? 2 : 5; // Black uses row 2, White uses row 5

        for (int col = 0; col < 8; col++) {
            Square square = board.getSquare(frontRow, col);
            if (square.hasPiece() && square.getColor() == this.color) {
                int dir = this.color ? 1 : -1; // Black moves down (+1), white up (-1)
                int newRow = frontRow + dir;

                // Try forward-left
                if (col - 1 >= 0 && board.getSquare(newRow, col - 1).getColor() == null) {
                    firstMoveSet.addNext(new Move(square, board.getSquare(newRow, col - 1)));
                }

                // Try forward-right
                if (col + 1 < 8 && board.getSquare(newRow, col + 1).getColor() == null) {
                    firstMoveSet.addNext(new Move(square, board.getSquare(newRow, col + 1)));
                }
            }
        }

        // Pick one random move from valid ones
        if (!firstMoveSet.getMoves().isEmpty()) {
            int pick = (int) (Math.random() * firstMoveSet.getMoves().size());
            Moves result = new Moves();
            result.addNext(firstMoveSet.getMoves().get(pick));
            return result;
        }

        return null; // No valid move found
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
    @Override
    public Moves requestMove(Board board) {
        this.currentGameBoard = board;

        // if (isFirstMove()) {
        // return startMove(board);
        // }

        // Future logic can go here for non-starting moves
        boolean strategy = playStyle();

        LinkedList<Pair<Square, LinkedList<MoveRating>>> possibleMoves = determineMoves(strategy);

        // Implement the bot's strategy based on the possible moves
        implementBotStrategy(strategy, possibleMoves);

        return sendMove(); // Send the moves to the game manager
    }

    /**
     * This function will be called to determine the all possible moves per piece
     * for BotII. Each moves for each piece will be assigned an elo. It will use the
     * gameboard given by the game manager to determine the possible moves through
     * the {@link BotII#requestMove()} function.
     * 
     * @param none
     * 
     * @return LinkedList<Pair<Square, LinkedList<MoveRating>>> - a list of pairs
     *         where each s
     * 
     * @see BotII#requestMove()
     * @see Board
     * @see Moves
     */
    private LinkedList<Pair<Square, LinkedList<MoveRating>>> determineMoves(boolean strategy) {

        // Gets possible moves per piece with each move having an elo rating
        LinkedList<Pair<Square, LinkedList<MoveRating>>> possibleMoves = new LinkedList<>();

        // Iterate through the board to find pieces belonging to this bot
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Square square = this.currentGameBoard.getSquare(row, col);

                // Check if the square contains a piece of this bot's color
                if (square.hasPiece() && square.getColor() == this.color) {
                    boolean isKing = square.isKing();

                    // Determine possible directions based on whether the piece is a king
                    char[] directions = isKing ? new char[] { 'F', 'B' } : new char[] { 'F' };

                    // Create a Moves object for this piece
                    LinkedList<MoveRating> pieceMoves = new LinkedList<>();

                    // Check moves in each direction
                    for (char direction : directions) {

                        // Check for normal moves (non-capturing)
                        Moves normalMoves = checkSingleMove(square, direction, false);

                        // If there are normal moves diagonally, add them to the pieceMoves object
                        if (!normalMoves.getMoves().isEmpty()) {
                            normalMoves.getMoves().forEach(x -> {
                                int rowsToKing = movesToKing(x.getDest(), this.color);

                                // Base elo for normal move
                                int elo = 1;

                                // Add rows to king to elo rating
                                elo += 7 - rowsToKing;
                                MoveRating mr = new MoveRating(x, elo);

                                pieceMoves.add(mr);
                            });
                        }

                        // Check for capturing moves either diagonal for each direction
                        Moves captureMove = checkSingleMove(square, direction, true);

                        if (!captureMove.getMoves().isEmpty()) {
                            normalMoves.getMoves().forEach(x -> {

                                int rowsToKing = movesToKing(x.getDest(), this.color);

                                // Base elo for capturing move
                                int elo = 3;

                                // Add rows to king to elo rating
                                elo += 7 - rowsToKing;

                                // Get the captured square (the square between start and dest) and check if it
                                // is a king
                                Square capturedSquare = this.currentGameBoard.getSquare(
                                        (x.getStart().getRow() + x.getDest().getRow()) / 2,
                                        (x.getStart().getCol() + x.getDest().getCol()) / 2);

                                boolean capturedIsKing = capturedSquare.isKing();

                                // Add elo based on whether the captured piece is a king, 5 for king
                                elo += capturedIsKing ? 5 : 0;

                                MoveRating moveRating = new MoveRating(x, elo);

                                pieceMoves.add(moveRating);
                            });
                        }
                    }
                    // Sort the pieceMoves based on elo rating
                    pieceMoves.sort(Comparator.comparingInt(MoveRating::getEloRating));

                    // If strategy is aggressive, reverse the order of the  (highest elo first)
                    if(strategy)
                        Collections.reverse(pieceMoves);

                    if (!pieceMoves.isEmpty()) {
                        // Add the piece and its possible moves to the list
                        possibleMoves.add(new Pair<>(square, pieceMoves));
                    }

                }
            }
        }

        return possibleMoves;
    }

    /**
     * Helper method to check if a single move is valid in a given direction.
     *
     * @param start     The starting square of the piece.
     * @param direction The direction of the move ('F' for forward, 'B' for
     *                  backward).
     * @param isCapture Whether the move is a capturing move.
     * @return A Move object if the move is valid, otherwise null.
     */
    private Moves checkSingleMove(Square start, char direction, boolean isCapture) {

        // Get the starting square's row for determining forward direction
        int pieceRow = start.getRow();

        // Determine forward direction for this piece
        int forward = (this.color && pieceRow > 4) || (!this.color && pieceRow < 3) ? 1 : -1;

        // Determine the row sign based on the direction
        int rowSign = (direction == 'F') ? forward : -forward;

        // Determine the step size based on whether it's a capturing move
        // For capturing moves, we need to move two squares forward
        int step = isCapture ? 2 : 1;

        int destRow = start.getRow() + (rowSign * step);
        int destColLeft = start.getCol() - step;
        int destColRight = start.getCol() + step;

        Moves diagonalMoves = new Moves();

        // Check left diagonal
        if (isValidMove(start, destRow, destColLeft, isCapture)) {
            diagonalMoves.addNext(new Move(start, this.currentGameBoard.getSquare(destRow, destColLeft)));
        }

        // Check right diagonal
        if (isValidMove(start, destRow, destColRight, isCapture)) {
            diagonalMoves.addNext(new Move(start, this.currentGameBoard.getSquare(destRow, destColRight)));
        }

        return diagonalMoves;
    }

    /**
     * Helper method to validate a move.
     *
     * @param start     The starting square of the piece.
     * @param destRow   The destination row.
     * @param destCol   The destination column.
     * @param isCapture Whether the move is a capturing move.
     * @return True if the move is valid, otherwise false.
     */
    private boolean isValidMove(Square start, int destRow, int destCol, boolean isCapture) {
        // Ensure the destination is within bounds
        if (destRow < 0 || destRow >= 8 || destCol < 0 || destCol >= 8) {
            return false;
        }

        Square dest = this.currentGameBoard.getSquare(destRow, destCol);

        // For normal moves, the destination must be empty
        if (!isCapture && !dest.hasPiece()) {
            return true;
        }

        // For capturing moves, check if the intermediate square contains an opponent's
        // piece
        if (isCapture) {
            // Ensure the destination square is empty
            if (dest.hasPiece()) {
                return false;
            }

            // Get the captured square (the square between start and dest)
            int midRow = (start.getRow() + destRow) / 2;
            int midCol = (start.getCol() + destCol) / 2;
            Square midSquare = this.currentGameBoard.getSquare(midRow, midCol);

            return midSquare.hasPiece() && midSquare.getColor() != this.color;
        }

        return false;
    }

    /**
     * Determines how many moves a piece is away from the back row (where it can
     * become a king).
     *
     * @param dest  The destination square of the piece.
     * @param color The color of the piece (true for black, false for white).
     * @return The number of moves to the back row.
     */
    private int movesToKing(Square dest, boolean color) {
        int destRow = dest.getRow();
        return color ? destRow : (7 - destRow);
    }

    /**
     * Implements BotII's strategy (predetermined) based on the moves it can make,
     * as determined by {@link BotII#determineMoves()}. Once determined, this method
     * will set the moves object to the list of moves that BotII will make.
     *
     * @param strategy - a boolean value indicating whether to implement the
     *
     * @return Nothing – the moves object is set to the list of moves that BotII
     *         will make
     *
     * @see BotII#determineMoves()
     * @see Moves
     * @see Board
     */
    private void implementBotStrategy(boolean strategy,
            LinkedList<Pair<Square, LinkedList<MoveRating>>> possibleMoves) {
        // ORDER OF HOW TO WRITE THIS FUNC
        // determine strategy
        // if passive , sort by ELo and use lowest
        // if aggr , sort by ELO and use highest

        // after set isMoves , add move to Move obj

        // Move m = possibleMoves.getFirst().getValue().getMove()
        // this.Move.addNext(m)
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

    /**
     * Determines whether the bot should use an aggressive strategy by counting the
     * pieces on the board.
     * For each square that has a piece, it checks the piece’s color (using
     * Square.getColor()). If the piece belongs to
     * the bot (using this bot's color stored in this.color), it increments the
     * bot's count;
     * otherwise, it increments the opponent's count.*
     * 
     * @param None
     * 
     * @return true if the bot is at a numerical disadvantage (suggesting an
     *         aggressive
     *         strategy), or false if not (suggesting a more passive strategy).
     * 
     */
    private boolean playStyle() {
        int bot_piece_cnt = 0;
        int op_piece_cnt = 0;

        // Iterate over the 8x8 board.
        for (int row = 0; row <= 7; row++) {
            for (int col = 0; col <= 7; col++) {
                Square square = this.currentGameBoard.getSquare(row, col);
                if (square.hasPiece()) { // Only count if there is a piece on the square.
                    if (square.getColor() == this.color) {
                        bot_piece_cnt++;
                    } else {
                        op_piece_cnt++;
                    }
                }
            }
        }
        // Return true if your bot has fewer pieces than its opponent which would result
        // in aggresive gameplay style by Bot
        return bot_piece_cnt > op_piece_cnt;
    }
}

class MoveRating {

    private Move move;
    private int eloRating;

    public MoveRating(Move move, int eloRating) {
        this.move = move;
        this.eloRating = eloRating;
    }

    public Move getMove() {
        return move;
    }

    public int getEloRating() {
        return eloRating;
    }

}

class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
