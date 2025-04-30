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
        System.out.println("[DEBUG BotII.requestMove] === BotII Requesting Move ===");
        setCurrentGameBoard(board);
        flushMoves();

        boolean strategy = playStyle();
        System.out.println("[DEBUG BotII.requestMove] Bot playstyle aggressive: " + strategy);

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

        boolean captureIsMandatory = rules.isCaptureAvailableForPlayer(tempGameContext);
        System.out.println("[DEBUG BotII.requestMove] rules.isCaptureAvailableForPlayer reports: " + captureIsMandatory);

        LinkedList<MoveRating> allPossibleRatedMoves = new LinkedList<>();
        LinkedList<Pair<Square, LinkedList<MoveRating>>> movesPerPiece = determineMoves(strategy);
        for (Pair<Square, LinkedList<MoveRating>> pair : movesPerPiece) {
            allPossibleRatedMoves.addAll(pair.getValue());
        }
        System.out.println("[DEBUG BotII.requestMove] Total rated moves generated by determineMoves: " + allPossibleRatedMoves.size());

        LinkedList<MoveRating> movesToConsider = new LinkedList<>();

        if (captureIsMandatory) {
            System.out.println("[DEBUG BotII.requestMove] Filtering FOR captures (mandatory).");
            int addedCaptures = 0;
            for (MoveRating mr : allPossibleRatedMoves) {
                int rowDiff = Math.abs(mr.getMove().getDest().getRow() - mr.getMove().getStart().getRow());
                if (rowDiff > 1) {
                    System.out.println("[DEBUG BotII.requestMove]   Adding mandatory capture move: (" + mr.getMove().getStart().getRow() + "," + mr.getMove().getStart().getCol() + ")->(" + mr.getMove().getDest().getRow() + "," + mr.getMove().getDest().getCol() + ") ELO: " + mr.getEloRating());
                    movesToConsider.add(mr);
                    addedCaptures++;
                } else {
                    System.out.println("[DEBUG BotII.requestMove]   Skipping non-jump move when capture mandatory: (" + mr.getMove().getStart().getRow() + "," + mr.getMove().getStart().getCol() + ")->(" + mr.getMove().getDest().getRow() + "," + mr.getMove().getDest().getCol() + ")");
                }
            }
            if (movesToConsider.isEmpty() && !allPossibleRatedMoves.isEmpty() && addedCaptures == 0) {
                System.err.println("[ERROR BotII.requestMove] Discrepancy: rules.isCaptureAvailable=true, but no JUMP moves found by determineMoves!");
            } else {
                System.out.println("[DEBUG BotII.requestMove]   Added " + addedCaptures + " capture moves to consideration list.");
            }

        } else {
            System.out.println("[DEBUG BotII.requestMove] Filtering FOR normal moves (capture not mandatory).");
            for (MoveRating mr : allPossibleRatedMoves) {
                int rowDiff = Math.abs(mr.getMove().getDest().getRow() - mr.getMove().getStart().getRow());
                if (rowDiff == 1) {
                    System.out.println("[DEBUG BotII.requestMove]   Adding normal move: (" + mr.getMove().getStart().getRow() + "," + mr.getMove().getStart().getCol() + ")->(" + mr.getMove().getDest().getRow() + "," + mr.getMove().getDest().getCol() + ") ELO: " + mr.getEloRating());
                    movesToConsider.add(mr);
                } else {
                    System.out.println("[DEBUG BotII.requestMove]   Skipping jump move when capture not mandatory: (" + mr.getMove().getStart().getRow() + "," + mr.getMove().getStart().getCol() + ")->(" + mr.getMove().getDest().getRow() + "," + mr.getMove().getDest().getCol() + ")");
                }
            }
        }

        movesToConsider.sort(Comparator.comparingInt(MoveRating::getEloRating));
        if (strategy) {
            Collections.reverse(movesToConsider);
            System.out.println("[DEBUG BotII.requestMove] Sorted moves (Aggressive - High ELO first)");
        } else {
            System.out.println("[DEBUG BotII.requestMove] Sorted moves (Passive - Low ELO first)");
        }
        for(MoveRating mr : movesToConsider) { System.out.println("  -> ("+mr.getMove().getStart().getRow()+","+mr.getMove().getStart().getCol()+") -> ("+mr.getMove().getDest().getRow()+","+mr.getMove().getDest().getCol()+") ELO: "+mr.getEloRating()); }


        if (this.moves == null) this.moves = new Moves();
        else this.moves.getMoves().clear();


        if (movesToConsider.isEmpty()) {
            System.err.println("[ERROR BotII.requestMove] No valid moves found to add after filtering/sorting.");
        } else {
            System.out.println("[DEBUG BotII.requestMove] Final ordered list has " + movesToConsider.size() + " potential moves. Adding to this.moves.");
            for (MoveRating mr : movesToConsider) {
                this.moves.addNext(mr.getMove());
            }
            if (this.moves.size() > 0) {
                Move bestMove = this.moves.getFirst();
                System.out.println("[DEBUG BotII.requestMove] Selected best move: ("+bestMove.getStart().getRow()+","+bestMove.getStart().getCol()+") -> ("+bestMove.getDest().getRow()+","+bestMove.getDest().getCol()+")");
            }
        }
        System.out.println("[DEBUG BotII.requestMove] === BotII Returning Move(s) ===");
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
        System.out.println("[DEBUG BotII.determineMoves] Starting move determination. Strategy aggressive: " + strategy);
        LinkedList<Pair<Square, LinkedList<MoveRating>>> possibleMoves = new LinkedList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = this.currentGameBoard.getSquare(row, col);

            if (square.hasPiece() && square.getColor() == this.color) {
                System.out.println("[DEBUG BotII.determineMoves] === Processing piece at (" + row + "," + col + ") ===");
                LinkedList<MoveRating> pieceMovesWithRatings = new LinkedList<>();

                int[] startCoords = {row, col};
                System.out.println("[DEBUG BotII.determineMoves]   Calling rules.getMovesForSquare for (" + row + "," + col + ")");
                Moves legalMovesForSquare = rules.getMovesForSquare(this.currentGameBoard, this.color, startCoords);

                if (legalMovesForSquare != null && legalMovesForSquare.getMoves() != null) {
                        System.out.println("[DEBUG BotII.determineMoves]   Result from rules.getMovesForSquare for (" + row + "," + col + "): " + legalMovesForSquare.size() + " moves found.");
                        for(Move m : legalMovesForSquare.getMoves()) {
                        if (m != null && m.getStart() != null && m.getDest() != null) {
                                System.out.println("[DEBUG BotII.determineMoves]   Raw move returned: ("+m.getStart().getRow()+","+m.getStart().getCol()+") -> ("+m.getDest().getRow()+","+m.getDest().getCol()+")");
                        }
                        }
                } else {
                        System.out.println("[DEBUG BotII.determineMoves]   Result from rules.getMovesForSquare for (" + row + "," + col + "): null or empty moves list.");
                }


                if (legalMovesForSquare != null && legalMovesForSquare.size() > 0) {
                    for (Move move : legalMovesForSquare.getMoves()) {
                        if (move == null || move.getStart() == null || move.getDest() == null) {
                            System.out.println("[DEBUG BotII.determineMoves]   Skipping null move object processing for piece at (" + row + "," + col + ")");
                            continue;
                        }

                            System.out.println("[DEBUG BotII.determineMoves]   --- Evaluating move: (" +
                                            move.getStart().getRow() + "," + move.getStart().getCol() + ") -> (" +
                                            move.getDest().getRow() + "," + move.getDest().getCol() + ") ---");

                        boolean isCaptureMove = rules.isCapture(move, this.currentGameBoard);
                        System.out.println("[DEBUG BotII.determineMoves]     Is capture? " + isCaptureMove);
                        int elo = 1;

                        if (isCaptureMove) {
                            elo = 3;
                            System.out.println("[DEBUG BotII.determineMoves]     Capture base elo: " + elo);
                            try {
                                int midR = (move.getStart().getRow() + move.getDest().getRow()) / 2;
                                int midC = (move.getStart().getCol() + move.getDest().getCol()) / 2;
                                Square capturedSquare = this.currentGameBoard.getSquare(midR, midC);
                                if (capturedSquare != null && capturedSquare.hasPiece() && capturedSquare.isKing()) {
                                    elo += 5;
                                        System.out.println("[DEBUG BotII.determineMoves]     Captured King bonus applied. New elo: " + elo);
                                } else if (capturedSquare == null || !capturedSquare.hasPiece()) {
                                    System.err.println("[ERROR BotII.determineMoves]     Logic error: isCapture=true but middle square ("+midR+","+midC+") invalid or empty for move (" + move.getStart().getRow() + "," + move.getStart().getCol() + ")->(" + move.getDest().getRow() + "," + move.getDest().getCol() + ")");
                                } else {
                                        System.out.println("[DEBUG BotII.determineMoves]     Captured non-king piece. No extra king bonus.");
                                }
                            } catch (Exception e) {
                                    System.err.println("[ERROR BotII.determineMoves]     Exception calculating captured square ELO bonus: " + e.getMessage());
                            }
                        }

                        int rowsToKing = movesToKing(move.getDest(), this.color);
                        elo += (7 - rowsToKing);
                            System.out.println("[DEBUG BotII.determineMoves]     Moves to king: " + rowsToKing + ", King proximity bonus: " + (7-rowsToKing) + ". New elo: " + elo);

                        MoveRating moveRating = new MoveRating(move, elo);
                            System.out.println("[DEBUG BotII.determineMoves]     Adding MoveRating: (" + move.getStart().getRow() + "," + move.getStart().getCol() + ")->(" + move.getDest().getRow() + "," + move.getDest().getCol() + ") with final ELO: " + elo);
                        pieceMovesWithRatings.add(moveRating);
                    }
                } else {
                        System.out.println("[DEBUG BotII.determineMoves]   No legal moves were returned for piece at (" + row + "," + col + ")");
                }

                if (!pieceMovesWithRatings.isEmpty()) {
                        pieceMovesWithRatings.sort(Comparator.comparingInt(MoveRating::getEloRating));
                        System.out.println("[DEBUG BotII.determineMoves]   Found " + pieceMovesWithRatings.size() + " rated moves for piece (" + row + "," + col + "). Adding Pair to possibleMoves.");
                        possibleMoves.add(new Pair<>(square, pieceMovesWithRatings));
                } else {
                        System.out.println("[DEBUG BotII.determineMoves]   No rated moves generated for piece (" + row + "," + col + ")");
                }
                    System.out.println("[DEBUG BotII.determineMoves] === Finished processing piece at (" + row + "," + col + ") ===");
            }
        }
    }
    System.out.println("[DEBUG BotII.determineMoves] Finished move determination. Total pieces with possible moves: " + possibleMoves.size());
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
        if (dest == null) return 7;
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
