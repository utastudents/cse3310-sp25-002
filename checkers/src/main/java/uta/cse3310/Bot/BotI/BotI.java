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
        setCurrentGameBoard(board);
        flushMoves();

        LinkedList<Move> possibleMoves = determineMoves(board);
        boolean isAggressive = isAggressive(board);

        Moves playMove;
        if (isAggressive) {
            playMove = aggressiveStrategyImplementation(possibleMoves, board);
        } else {
            playMove = passiveStrategyImplementation(possibleMoves, board);
        }

        this.moves = playMove;
        return sendMove();

    }

    /* Sending Moves from Bot 1 to the GameManager */
    @Override
    protected Moves sendMove() {
        return this.moves;
    }

    private boolean isAggressive(Board board) {
        int botPieces = countallPieces(board, color);
        int playerPieces = countallPieces(board, color);

        boolean isAggressive = botPieces < playerPieces;
        return isAggressive;
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

    private boolean isCapturingMove(Move move, Board board) {
        Square start = move.getStart();
        Square end = move.getDest();

        if (start.getRow() - end.getRow() == 2) {
            int jumpedRow = (start.getRow() + end.getRow() / 2);
            int jumpedColumn = (start.getCol() + end.getCol() / 2);

            Square jumpedSquare = board.getSquare(jumpedRow, jumpedColumn);

            if ((jumpedSquare.getColor() != null) && (jumpedSquare.getColor() != end.getColor())) {
                return true;
            }
        }

        return false;
    }

    private Moves aggressiveStrategyImplementation(LinkedList<Move> possibleMoves, Board board) {
        // similar implementation to passive strategy BUT...
        // bot's goal is so maximize the number of capture moves

        Move bestMove = null;
        int bestScore = -1;

        for (Move move : possibleMoves) {
            int score = 1;

            // incremement for being a better move
            if (isCapturingMove(move, board)) {
                score += 1;
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        // if no move selected, pick the first
        if (bestMove == null && !possibleMoves.isEmpty()) {
            bestMove = possibleMoves.getFirst();
        }

        Moves result = new Moves();
        if (bestMove != null) {
            result.addNext(bestMove);
        }
        return result;
    }

    /**
     * Chooses a move using a basic passive strategy.
     * Prefers safer moves with minimal risk.
     *
     * @param possibleMoves A list of possible moves the player can make.
     * @param board         The current state of the game board.
     * @return A Moves object containing the selected move (or first move if none
     *         preferred).
     */
    private Moves passiveStrategyImplementation(LinkedList<Move> possibleMoves, Board board) {
        Move bestMove = null;
        int bestPreference = -1;

        for (Move move : possibleMoves) {

            // Skip risky moves
            if (insideDangerRegion(move, board))
                continue;

            int prefernceScore = 0;

            prefernceScore += 1;

            if (!move.getStart().isKing()) {
                int startRow = move.getStart().getRow();
                int destRow = move.getDest().getRow();

                if (!this.color && destRow < startRow)
                    prefernceScore += 1;
                else if (this.color && destRow > startRow)
                    prefernceScore += 1;
            }

            int col = move.getDest().getCol();
            if (col == 3 || col == 4) {
                prefernceScore += 2;
            } else if (col == 2 || col == 5) {
                prefernceScore += 1;
            }

            if (hasBackupAfterMove(move, board)) {
                prefernceScore += 1;
            }

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

    private boolean insideDangerRegion(Move move, Board board) {
        // function serves to tell if a piece is in danger of being captured after a
        // move is made
        int row = move.getDest().getRow();
        int col = move.getDest().getCol();
        int enemyDir = this.color ? -1 : 1;

        return canBeAttackedFrom(row, col, row + enemyDir, col - 1, board) ||
                canBeAttackedFrom(row, col, row + enemyDir, col + 1, board);
    }

    private boolean canBeAttackedFrom(int targetRow, int targetCol, int attackerRow, int attackerCol, Board board) {
        if (!isInsideBoard(attackerRow, attackerCol))
            return false;

        Square attacker = board.getSquare(attackerRow, attackerCol);
        return attacker.hasPiece() && attacker.getColor() != this.color;
    }

    // Checks if the move ends on a square protected by an ally
    private boolean hasBackupAfterMove(Move move, Board board) {
        int row = move.getDest().getRow();
        int col = move.getDest().getCol();
        int friendDir = this.color ? -1 : 1;

        return hasSupportAt(row + friendDir, col - 1, board) || hasSupportAt(row + friendDir, col + 1, board);
    }

    private boolean hasSupportAt(int row, int col, Board board) {
        if (!isInsideBoard(row, col))
            return false;
        Square square = board.getSquare(row, col);
        return square.hasPiece() && square.getColor() == this.color;
    }

    // Utility method to check board boundaries
    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

}
