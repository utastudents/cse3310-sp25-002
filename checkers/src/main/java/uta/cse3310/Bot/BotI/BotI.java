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
            // aggressiveStrategyImplementation(LinkedList<Move> possibleMoves);
        } else {
            // passiveStrategyImplementation(possibleMoves);
        }

    }

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
        int movement = this.color ? 1 : -1; // if true: white, false: black

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = board.getSquare(row, col);
                if (square.hasPiece() && square.getColor() == this.color) {
                    // Normal moves
                    playNormalMove(validMoves, square, row + movement, col - 1, board); // move left
                    playNormalMove(validMoves, square, row + movement, col + 1, board); // move right

                    // King moves backward as well
                    if (square.isKing()) {
                        playNormalMove(validMoves, square, row - movement, col - 1, board);
                        playNormalMove(validMoves, square, row - movement, col + 1, board);
                    }

                    // Captures
                    playCapture(validMoves, square, row + 2 * movement, col - 2, row + movement,
                            col - 1, board);
                    playCapture(validMoves, square, row + 2 * movement, col + 2, row + movement,
                            col + 1, board);
                    if (square.isKing()) {
                        playCapture(validMoves, square, row - 2 * movement, col - 2, row - movement,
                                col - 1, board);
                        playCapture(validMoves, square, row - 2 * movement, col + 2, row - movement,
                                col + 1, board);
                    }
                }
            }
        }
        return validMoves;
    }

    private void playNormalMove(LinkedList<Move> moves, Square square, int toRow, int toCol, Board board) {
        // Add logic here
        if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
            Square newPosition = board.getSquare(toRow, toCol);
            if (!newPosition.hasPiece()) { // check and play normal move when the new position is empty
                moves.add(new Move(square, newPosition));
            }
        }

    }

    private void playCapture(LinkedList<Move> moves, Square square, int toRow, int toCol, int midRow, int midCol,
            Board board) {
        // Add logic here
        if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
            Square newPosition = board.getSquare(toRow, toCol);
            Square midPosition = board.getSquare(midRow, midCol);

            // Now the capture can be done only when the new position is empty and the mid
            // position has a opponent piece
            if (!newPosition.hasPiece() && midPosition.hasPiece() && midPosition.getColor() != this.color) {
                moves.add(new Move(square, newPosition));
            }
        }
    }

    /* Adding the skeleton for Passive and Aggressive Strategy */
    private Moves passiveStrategyImplementation(LinkedList<Move> possibleMoves, Board board) {
        Move bestMove = null;
        int bestPreference = -1;

        for (Move move : possibleMoves) {

            // Skip risky moves
            // if (isInDangerZone(move, board))
            // continue;

            int prefernceScore = 0;

            prefernceScore += 1;

            // hasBackupAfterMove — +1 if protected
            // if (hasBackupAfterMove(move, board)) {
            // prefernceScore += 1;
            // }

            if (prefernceScore > bestPreference) {
                bestPreference = prefernceScore;
                bestMove = move;
            }

        }
        // If all the moves are isInDangerZone pick the first move
        if (bestMove == null && !possibleMoves.isEmpty()) {
            bestMove = possibleMoves.getFirst();
        }

        Moves result = new Moves();
        if (bestMove != null) {
            result.addNext(bestMove);
        }
        return result;
    }
    /* Helper Methods for passiveStrategyImplementation */
}
