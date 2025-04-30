package uta.cse3310.Bot.BotI;

import uta.cse3310.Bot.Bot;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Game;
import java.util.LinkedList;
import uta.cse3310.GamePlay.rules;
import java.util.Collections;
import java.util.Comparator;


public class BotI extends Bot {

    public BotI() {
        super(); // Call the constructor of the parent class (Bot)
    }

    @Override
    public Moves requestMove(Board board) {
        setCurrentGameBoard(board);
        flushMoves();


        Game tempGameContext = new Game(
                this.color ? 0 : 1,
                this.color ? 1 : 0,
                this.color,
                !this.color,
                999,
                "BotITemp", "OppTemp"
        );
        tempGameContext.updateBoard(this.currentGameBoard.copy());
        while (tempGameContext.getCurrentTurn().getColor() != this.color) {
            tempGameContext.switchTurn();
        }
        boolean captureIsMandatory = rules.isCaptureAvailableForPlayer(tempGameContext);
        System.out.println("[DEBUG BotI] rules.isCaptureAvailableForPlayer reports: " + captureIsMandatory);


        LinkedList<Move> allPossibleMoves = determineMoves(board);
        LinkedList<Move> movesToConsider = new LinkedList<>();

        if (captureIsMandatory) {
            System.out.println("[DEBUG BotI] Filtering FOR captures.");
            for (Move move : allPossibleMoves) {
                if (rules.isCapture(move, this.currentGameBoard)) {
                    movesToConsider.add(move);
                }
            }
            if (movesToConsider.isEmpty() && !allPossibleMoves.isEmpty()) {
                System.err.println("[ERROR BotI] Discrepancy: rules.isCaptureAvailable=true, but no capture moves found by determineMoves!");
            }
        } else {
            System.out.println("[DEBUG BotI] Filtering FOR normal moves.");
            for (Move move : allPossibleMoves) {
                if (!rules.isCapture(move, this.currentGameBoard)) {
                    movesToConsider.add(move);
                }
            }
        }

        boolean isAggressive = isAggressive(board);
        movesToConsider.sort((move1, move2) -> {
            int score1 = calculateMoveScore(move1, board, isAggressive);
            int score2 = calculateMoveScore(move2, board, isAggressive);
            return isAggressive ? Integer.compare(score2, score1) : Integer.compare(score1, score2);
        });

        if (this.moves == null) this.moves = new Moves();
        else this.moves.getMoves().clear();

        if (movesToConsider.isEmpty()) {
            System.err.println("[ERROR BotI] No valid moves to return after filtering/sorting.");
        } else {
            System.out.println("[DEBUG BotI] Providing ordered list of " + movesToConsider.size() + " potential moves.");
            for (Move move : movesToConsider) {
                this.moves.addNext(move);
            }
        }

        return sendMove();
    }


    private int calculateMoveScore(Move move, Board board, boolean isAggressive) {
        int score = 0;
        boolean isCapture = rules.isCapture(move, board);

        if (isAggressive) {
            score = isCapture ? 100 : 1;
            if (!move.getStart().isKing() && ((move.getStart().getColor() && move.getDest().getRow() == 0) ||( !move.getStart().getColor() && move.getDest().getRow() == 7))) {
                score += 50;
            }
        } else {
            score = isCapture ? 10 : 0;
            if (insideDangerRegion(move, board)) {
                score += 500;
            }
            if (!move.getStart().isKing()) {
            int startRow = move.getStart().getRow();
            int destRow = move.getDest().getRow();
            if ((this.color && destRow > startRow) || (!this.color && destRow < startRow)) {
                score += 5;
            }
        }
        int col = move.getDest().getCol();
        if (col == 0 || col == 7) score += 3;
        else if (col == 1 || col == 6) score += 2;
        else if (col == 2 || col == 5) score += 1;
        if (!hasBackupAfterMove(move, board)) {
            score += 20;
        }
    }
        return score;
    }




    /* Sending Moves from Bot 1 to the GameManager */
    @Override
    protected Moves sendMove() {
        return this.moves;
    }

    protected boolean isAggressive(Board board) {
        int botPieces = countallPieces(board, this.color);
        int playerPieces = countallPieces(board, !(this.color));

        boolean isAggressive = botPieces < playerPieces;
        return isAggressive;
    }

    private int countallPieces(Board board, boolean color) {

        int count = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square selectsquare = board.getSquare(row, col);

                if (selectsquare.hasPiece() && selectsquare.getColor() == color) {
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
        int movement = this.color ? -1 : 1;

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
    protected void playNormalMove(LinkedList<Move> moves, Square square, int toRow, int toCol, Board board) {
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
    protected void playCapture(LinkedList<Move> moves, Square square, int toRow, int toCol, int midRow, int midCol,
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
        int distdifferenceRow = Math.abs(start.getRow() - end.getRow());
        int distdifferenceColumn = Math.abs(start.getCol() - end.getCol());

        // Piece moves 2 space; 1 of them over opponent piece
        if (distdifferenceRow - distdifferenceColumn == 2) {
            int jumpedRow = ((start.getRow() + end.getRow()) / 2);
            int jumpedColumn = ((start.getCol() + end.getCol()) / 2);

            Square jumpedSquare = board.getSquare(jumpedRow, jumpedColumn);
            Boolean movingPiece = start.getColor();
            Boolean jumpedpieceColor = jumpedSquare.getColor();

            return ((jumpedpieceColor != null && jumpedSquare.hasPiece()) && (!jumpedpieceColor == movingPiece));
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

            int preferenceScore = 0;

            preferenceScore += 1;

            if (!move.getStart().isKing()) {
                int startRow = move.getStart().getRow();
                int destRow = move.getDest().getRow();

                if (!this.color && destRow < startRow)
                    preferenceScore += 1;
                else if (this.color && destRow > startRow)
                    preferenceScore += 1;
            }

            int col = move.getDest().getCol();
            if (col == 3 || col == 4) {
                preferenceScore += 2;
            } else if (col == 2 || col == 5) {
                preferenceScore += 1;
            }

            if (hasBackupAfterMove(move, board)) {
                preferenceScore += 1;
            }

            if (preferenceScore > bestPreference) {
                bestPreference = preferenceScore;
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
    // Check if attacker position is valid
    if (!isInsideBoard(attackerRow, attackerCol)) return false;   
        // Get the potential attacker square
    Square attacker = board.getSquare(attackerRow, attackerCol);

    // Check if there's a piece and it's the opponent's
    if (!attacker.hasPiece() || attacker.getColor() == this.color) return false;

    // Calculate where the attacker would land if it captured
    int landingRow = attackerRow + (attackerRow - targetRow);
    int landingCol = attackerCol + (attackerCol - targetCol);

    // Landing position must be on the board and empty
    if (!isInsideBoard(landingRow, landingCol)) return false;

    Square landing = board.getSquare(landingRow, landingCol);
    return !landing.hasPiece();
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
