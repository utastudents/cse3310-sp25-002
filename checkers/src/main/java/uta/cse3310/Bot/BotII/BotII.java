package uta.cse3310.Bot.BotII;
import uta.cse3310.GamePlay.rules;
import uta.cse3310.GameManager.Game;
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
    public BotII() {
        super(); // Call the constructor of the parent class (Bot)
    }

    /**
     * Handles the first move logic for BotII.
     * It checks all pieces in the front row and tries to move them diagonally if
     * possible.
     *
     * @param board the current game board
     * @return a single randomly chosen move if valid ones exist, null otherwise
     */
    private Moves startMove() {
        Moves firstMoveSet = new Moves(); // Collect valid first moves

        int frontRow = this.color ? 2 : 5; // Black uses row 2, White uses row 5

        for (int col = 0; col < 8; col++) {
            Square square = this.currentGameBoard.getSquare(frontRow, col);
            if (square.hasPiece() && square.getColor() == this.color) {
                int dir = this.color ? 1 : -1; // Black moves down (+1), white up (-1)
                int newRow = frontRow + dir;

                // Try forward-left
                if (col - 1 >= 0 && this.currentGameBoard.getSquare(newRow, col - 1).getColor() == null) {
                    firstMoveSet.addNext(new Move(square, this.currentGameBoard.getSquare(newRow, col - 1)));
                }

                // Try forward-right
                if (col + 1 < 8 && this.currentGameBoard.getSquare(newRow, col + 1).getColor() == null) {
                    firstMoveSet.addNext(new Move(square, this.currentGameBoard.getSquare(newRow, col + 1)));
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
     * Checks if it's the bot's first move.
     * This is true if:
     * - All bot pieces are still in their default starting row
     * AND
     * - The opponent has made 0 or 1 moves (meaning the bot is either going first or second)
     *
     * @param board The current game board
     * @return true if this is BotII's first move
     */
    private boolean isFirstMove() {
        
        int botRow = this.color ? 2 : 5;
        int opponentRow = this.color ? 5 : 2;
        boolean opponentColor = !this.color;

        // 1. Check if all bot pieces are still in their starting row
        boolean allBotInDefaultRow = true;
        for (int col = 0; col < 8; col++) {
            Square sq = this.currentGameBoard.getSquare(botRow, col);
            if (!sq.hasPiece() || sq.getColor() != this.color) {
                allBotInDefaultRow = false;
                break;
            }
        }

        if (!allBotInDefaultRow) return false; // Bot already moved

        // 2. Check how many opponent pieces have moved from their starting row
        int movedOpponentPieces = 0;
        for (int col = 0; col < 8; col++) {
            Square sq = this.currentGameBoard.getSquare(opponentRow, col);
            if (!sq.hasPiece() || sq.getColor() != opponentColor) {
                movedOpponentPieces++;
            }
        }

        // If opponent has moved 0 or 1 piece, it's bot's first move
        return movedOpponentPieces <= 1;
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

        // Clear the moves object before making a new move
        setCurrentGameBoard(board);
        flushMoves();

        // Set the current game board to the one provided by the game manager

        // if (isFirstMove()) {
        //     return startMove();
        // }

        // Determine the bot's strategy based on the game state
        // If the bot is at a numerical disadvantage, it will play aggressively
        boolean strategy = playStyle();

        Game tempGameContext = new Game(
                this.color ? 0 : 1,
                this.color ? 1 : 0,
                this.color,
                !this.color,
                999,
                "BotTemp", "OppTemp"
        );
        tempGameContext.updateBoard(this.currentGameBoard.copy());

        while (tempGameContext.getCurrentTurn().getColor() != this.color) {
            tempGameContext.switchTurn();
        }

        boolean captureIsPossible = rules.isCaptureAvailableForPlayer(tempGameContext);
        System.out.println("[DEBUG BotII] rules.isCaptureAvailableForPlayer reports: " + captureIsPossible);

        LinkedList<MoveRating> allPossibleRatedMoves = new LinkedList<>();
        LinkedList<Pair<Square, LinkedList<MoveRating>>> movesPerPiece = determineMoves(strategy);
        for (Pair<Square, LinkedList<MoveRating>> pair : movesPerPiece) {
            allPossibleRatedMoves.addAll(pair.getValue());
        }


        LinkedList<MoveRating> movesToConsider = new LinkedList<>();
        if (captureIsPossible) {
            System.out.println("[DEBUG BotII] Filtering FOR captures based on rules check.");
            for (MoveRating mr : allPossibleRatedMoves) {
                if (rules.isCapture(mr.getMove(), this.currentGameBoard)) {
                    movesToConsider.add(mr);
                }
            }
            if (movesToConsider.isEmpty() && !allPossibleRatedMoves.isEmpty()) {
                System.err.println("[ERROR BotII] Discrepancy: rules.isCaptureAvailable=true, but no capture moves found by determineMoves!");
            }

        } else {
            System.out.println("[DEBUG BotII] Filtering AGAINST captures based on rules check.");
            for (MoveRating mr : allPossibleRatedMoves) {
                if (!rules.isCapture(mr.getMove(), this.currentGameBoard)) {
                    movesToConsider.add(mr);
                }
            }
        }

        movesToConsider.sort(Comparator.comparingInt(MoveRating::getEloRating));
        if (strategy) {
            Collections.reverse(movesToConsider);
        }

        if (this.moves == null) this.moves = new Moves();
        else this.moves.getMoves().clear();


        if (movesToConsider.isEmpty()) {
            System.err.println("[ERROR BotII] No valid moves found to add after filtering/sorting.");
        } else {
            System.out.println("[DEBUG BotII] Providing ordered list of " + movesToConsider.size() + " potential moves.");
            for (MoveRating mr : movesToConsider) {
                this.moves.addNext(mr.getMove());
            }
        }


        return sendMove();
    }


    private void implementBotStrategyFromList(boolean strategy, LinkedList<MoveRating> possibleMoves) {
        if (this.moves == null) this.moves = new Moves();
        else this.moves.getMoves().clear();

        if (possibleMoves == null || possibleMoves.isEmpty()) {
            System.err.println("[ERROR BotII] implementBotStrategyFromList called with no possible moves. Cannot make a move.");
            return;
        }

        Move m = possibleMoves.getFirst().getMove();
        this.moves.addNext(m);
        System.out.println("[DEBUG BotII] Selected move: " + m.getStart().getRow()+","+m.getStart().getCol() + " -> " + m.getDest().getRow()+","+m.getDest().getCol());
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
                    LinkedList<MoveRating> pieceMovesWithRatings = new LinkedList<>();

                    Moves legalMovesForSquare = rules.getMovesForSquare(this.currentGameBoard, this.color, new int[]{row, col});


                    if (legalMovesForSquare != null && legalMovesForSquare.size() > 0) {
                        for (Move move : legalMovesForSquare.getMoves()) {
                            if (move == null || move.getStart() == null || move.getDest() == null) continue;

                            boolean isCaptureMove = rules.isCapture(move, this.currentGameBoard);
                            int elo = 1;

                            if (isCaptureMove) {
                                elo = 3;
                                Square capturedSquare = this.currentGameBoard.getSquare(
                                    (move.getStart().getRow() + move.getDest().getRow()) / 2,
                                    (move.getStart().getCol() + move.getDest().getCol()) / 2);
                                if (capturedSquare != null && capturedSquare.isKing()) {
                                    elo += 5;
                                }
                            }

                            int rowsToKing = movesToKing(move.getDest(), this.color);
                            elo += (7 - rowsToKing);

                            MoveRating moveRating = new MoveRating(move, elo);
                            pieceMovesWithRatings.add(moveRating);
                        }
                    }
                    pieceMovesWithRatings.sort(Comparator.comparingInt(MoveRating::getEloRating));
                    if (!pieceMovesWithRatings.isEmpty()) {
                        possibleMoves.add(new Pair<>(square, pieceMovesWithRatings));
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
        int forwardDirection = this.color ? -1 : 1;

        // Determine the row sign based on the direction
        int rowSign = (direction == 'F') ? forwardDirection : -forwardDirection;

        // Determine the step size based on whether it's a capturing move
        // For capturing moves, we need to move two squares forward
        int step = isCapture ? 2 : 1;

        int destRow = start.getRow() + (rowSign * step);
        int destColLeft = start.getCol() - step;
        int destColRight = start.getCol() + step;

        Moves diagonalMoves = new Moves();

        // Check left diagonal
        if (isValidMove(start, destRow, destColLeft, isCapture)) {
            Square destSquare = this.currentGameBoard.getSquare(destRow, destColLeft);
            if (destSquare != null) {
                diagonalMoves.addNext(new Move(start, destSquare));
            }
        }

        // Check right diagonal
        if (isValidMove(start, destRow, destColRight, isCapture)) {
            Square destSquare = this.currentGameBoard.getSquare(destRow, destColRight);
            if (destSquare != null) {
                diagonalMoves.addNext(new Move(start, destSquare));
            }
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
        // if aggr , sort by ELO and use highest
        // // if passive , sort by ELo and use lowest
        if (possibleMoves == null || possibleMoves.isEmpty()) {
        System.out.println("[WARN BotII] implementBotStrategy called with no possible moves. Returning empty move.");
        if (this.moves == null) {
            this.moves = new Moves();
        } else {
            this.moves.getMoves().clear();
        }
        return;
    }
        possibleMoves.sort(Comparator.comparing((Pair<Square, LinkedList<MoveRating>> pair) -> pair.getValue().getFirst().getEloRating()));

        // if strategy is aggressive, reverse the order of the list (highest elo first)
        if (strategy)
            Collections.reverse(possibleMoves);

        if (possibleMoves.getFirst().getValue() == null || possibleMoves.getFirst().getValue().isEmpty()) {
            System.err.println("[ERROR BotII] implementBotStrategy: Found Pair with empty MoveRating list!");
            if (this.moves == null) {
                this.moves = new Moves();
            } else {
                this.moves.getMoves().clear();
            }
            return;
        }


        // after set isMoves , add move to Move obj
        Move m = possibleMoves.getFirst().getValue().getFirst().getMove();
        if (this.moves == null) {
            this.moves = new Moves();
        }
        this.moves.addNext(m);
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
     * otherwise, it increments the opponent's count.
     * @param None
     * @return true if the bot is at a numerical disadvantage (suggesting an
     *         aggressive
     *         strategy), or false if not (suggesting a more passive strategy).
     */
    private boolean playStyle() {
        int bot_piece_cnt = 0;
        int op_piece_cnt = 0;

        // Iterate over the 8x8 board.
        for (int row = 0; row < 8 ; row++) {
            for (int col = 0; col < 8; col++) {
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
