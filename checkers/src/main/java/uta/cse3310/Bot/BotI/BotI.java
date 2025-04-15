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

        // Determine all legal moves available in the current board state.
        LinkedList<Move> possibleMoves = determineMoves(board);

        // Simple heuristic: if our piece count is lower than the opponent's, play aggressively.
        int myCount = countallPieces(board, this.color);
        int oppCount = countallPieces(board, !this.color);
        boolean isAggressive = (myCount < oppCount);

        Move selectedMove;
        if (isAggressive) {
            selectedMove = aggressiveStrategyImplementation(possibleMoves, board);
        } else {
            selectedMove = passiveStrategyImplementation(possibleMoves, board);
        }

        // Add the selected move to the moves container and send it to the GameManager.
        this.moves.add(selectedMove);
        return sendMove();
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
        LinkedList<Move> openmoves = determineMoves(board);

        int bot_pieces = countallPieces(board, color);
        int player_pieces = countallPieces(board, color);

        boolean isAggresive = (bot_pieces < player_pieces);

        Move atMove;
        if (isAggresive) {
            // atMove = aggressiveStrategyImplementation(openmoves, board);
        } else {
            // atMove = passiveStrategyImplementation(openmoves, board);
        }

        // this.moves.addNext(atMove);
        // return sendMove();
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
            // passiveStrategyImplementation(possibleMoves,board);
        }

    }

    /**
     * determineMoves(Board board)
     * This method finds all valid moves for the current player.
     * It checks normal moves and capturing moves for all the player's pieces.
     * King pieces are also allowed to move and capture backward.
     * 
     * @param board The current game board.
     * @return A LinkedList of possible moves for the bot.
     */
    private LinkedList<Move> determineMoves(Board board) {
        LinkedList<Move> validMoves = new LinkedList<>();
        int movement = this.color ? 1 : -1;

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

    /* Play a normal non capturing move if the sqaure is empty */
    private void playNormalMove(LinkedList<Move> moves, Square square, int toRow, int toCol, Board board) {
        if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
            Square newPosition = board.getSquare(toRow, toCol);
            if (!newPosition.hasPiece()) {
                moves.add(new Move(square, newPosition));
            }
        }

    }

    /*
     * Play a capture move if the middle square has an opponent's piece and the
     * target is empty
     */
    private void playCapture(LinkedList<Move> moves, Square square, int toRow, int toCol, int midRow, int midCol,
            Board board) {
        if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
            Square newPosition = board.getSquare(toRow, toCol);
            Square midPosition = board.getSquare(midRow, midCol);

            if (!newPosition.hasPiece() && midPosition.hasPiece() && midPosition.getColor() != this.color) {
                moves.add(new Move(square, newPosition));
            }
        }
    }

    private Moves aggressiveStrategyImplementation(LinkedList<Move> possibleMoves, Board board) {
        // similar implementation to passive strategy BUT...
        // bot's goal is so maximize the number of capture moves

        // initialize a variable which starts with no move (make in null)
        // loop through all possible moves
        // increment a variable that judges how good the move is (higher score = better
        // move)
        // every move should be initialized to 1
        // (this is all from a defensive POV)

        // if the move has a better score than the current best move, update the best
        // move variable to the move that has a better score
        // if no move was safe for the bot && there are no possible moves -> pick the
        // first move to make game continue
        // put the resulting move into a Moves object and return it

        return null;
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
