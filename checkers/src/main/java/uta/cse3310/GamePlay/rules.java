package uta.cse3310.GamePlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GameManager.Square;




//class rules checks if the move is legal
public class rules
{
    //checks if the move is within the bounds of the board
    //8x8 board
    public static boolean inBounds(Move move)
    {
        Square square = move.getDest();
        if (square == null) return false;

        int row = square.getRow();
        int col = square.getCol();

        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public static boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

 

    public static boolean isCaptureAvailableFromSquare(Game game, Square startSquare) {
        if (game == null || startSquare == null) {
            System.out.println("[DEBUG rules.isCaptureAvailableFromSquare] Null game or startSquare provided.");
            return false;
        }
        Board board = game.getBoard();
        Player player = game.getCurrentTurn();

        if (board == null || player == null || !startSquare.hasPiece() || startSquare.getColor() != player.getColor()) {
            System.out.println("[DEBUG rules.isCaptureAvailableFromSquare] Invalid start square for check: " + (startSquare != null ? "("+startSquare.getRow()+","+startSquare.getCol()+")" : "null") + ", Player: " + (player != null ? player.getPlayerId() : "null") + ", PieceColorMatch: " + (startSquare != null && player != null && startSquare.hasPiece() ? (startSquare.getColor() == player.getColor()) : "N/A"));
            return false;
        }

        System.out.println("[DEBUG rules.isCaptureAvailableFromSquare] Checking captures for Player " + player.getPlayerId() + " from square ("+startSquare.getRow()+","+startSquare.getCol()+")");
        return hasLegalCaptureFromSquare(game, startSquare);
    }


    public static boolean isCapture(Move move, Board board) {
        Square start = move.getStart();
        Square dest = move.getDest();


        int rowDiff = Math.abs(dest.getRow() - start.getRow());
        int colDiff = Math.abs(dest.getCol() - start.getCol());

        if (rowDiff == 2 && colDiff == 2) {
            int middleRow = (start.getRow() + dest.getRow()) / 2;
            int middleCol = (start.getCol() + dest.getCol()) / 2;

            if (!inBounds(middleRow, middleCol)) return false;

            Square middleSquare = board.getSquare(middleRow, middleCol);

            boolean logic = middleSquare != null && middleSquare.hasPiece() && middleSquare.getColor() != start.getColor();
            return logic;
        }
        return false;
    }

    //checks if a move is legal based on checkers rules
    //returns true if:
    // - the destination square is unoccupied AND
    // - the move is either a regular 1-step diagonal move
    // - OR a 2-step jump over an opponent's piece
    //returns false in all other cases
    // During testing, make methods for repetetive lines to reduce file length
    static protected boolean isLegal(Move move, Game game) {
        Square start = move.getStart();
        Square dest = move.getDest();

        if (start == null || dest == null) return false;

        Board board = game.getBoard();
        Player player = game.getCurrentTurn();

        if (board == null || player == null) return false;

        Square boardStart = board.getSquare(start.getRow(), start.getCol());
        Square boardDest = board.getSquare(dest.getRow(), dest.getCol());


        if (boardStart == null || boardDest == null || !boardStart.hasPiece() || boardStart.getColor() != player.getColor()) {
            System.out.println("[DEBUG rules.isLegal] Fail: Start/Dest null from board, No piece, or Wrong color piece");
            return false;
        }
        if (boardDest.hasPiece()) {
            System.out.println("[DEBUG rules.isLegal] Fail: Dest occupied");
            return false;
        }

        boolean playerColor = player.getColor();
        int rowDiff = boardDest.getRow() - boardStart.getRow();
        int colDiffAbs = Math.abs(boardDest.getCol() - boardStart.getCol());
        int rowDiffAbs = Math.abs(rowDiff);

        if (rowDiffAbs == 1 && colDiffAbs == 1) {
            // Check direction
            if (boardStart.isKing()) {
                System.out.println("[DEBUG rules.isLegal] OK: King normal move");
                return true;
            } else {
                if ((playerColor && rowDiff == -1) || (!playerColor && rowDiff == 1)) {
                    System.out.println("[DEBUG rules.isLegal] OK: Regular normal forward move");
                    return true;
                } else {
                    System.out.println("[DEBUG rules.isLegal] Fail: Regular normal backward move");
                    return false;
                }
            }
        }
        else if (rowDiffAbs == 2 && colDiffAbs == 2) {
            if (!boardStart.isKing() && !((playerColor && rowDiff == -2) || (!playerColor && rowDiff == 2))) {
            System.out.println("[DEBUG rules.isLegal] Fail: Regular jump backward move");
                return false;
            }

            int middleRow = (boardStart.getRow() + boardDest.getRow()) / 2;
            int middleCol = (boardStart.getCol() + boardDest.getCol()) / 2;

            Square middleSquare = board.getSquare(middleRow, middleCol);

            if (middleSquare != null && middleSquare.hasPiece() && middleSquare.getColor() != playerColor) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public static boolean isCaptureAvailableForPlayer(Game game) {
        Board board = game.getBoard();
        Player player = game.getCurrentTurn();

        if (board == null || player == null) {
            System.err.println("[ERROR rules.isCaptureAvailableForPlayer] Game, Board or Player is null.");
            return false;
        }

        boolean playerColor = player.getColor();
        System.out.println("[DEBUG rules.isCaptureAvailableForPlayer] Checking for color: " + (playerColor ? "White" : "Black")); // Added TEMP_DEBUG

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Square start = board.getSquare(r, c);
                if (start != null && start.hasPiece() && start.getColor() != null && start.getColor() == playerColor) {
                    if (hasLegalCaptureFromSquare(game, start)) {
                        System.out.println("[DEBUG rules.isCaptureAvailableForPlayer] Capture found for piece at ("+r+","+c+")");
                        return true;
                    }
                }
            }
        }
        System.out.println("[DEBUG rules.isCaptureAvailableForPlayer] No captures found for player " + (playerColor ? "White" : "Black"));
        return false;
    }


    static protected boolean hasLegalCaptureFromSquare(Game game, Square start) {
        Board board = game.getBoard();
        if (board == null || start == null || !start.hasPiece() || start.getColor() == null) {
            return false;
        }


        boolean playerColor = start.getColor();
        int row = start.getRow();
        int col = start.getCol();

        int[] rowOffsets = start.isKing() ? new int[]{-2, 2} : (playerColor ? new int[]{-2} : new int[]{2}); // White(true) moves -2, Black(false) moves +2
        int[] colOffsets = {-2, 2};
        System.out.println("[DEBUG rules.hasLegalCaptureFromSquare] Checking piece at ("+row+","+col+"), King="+start.isKing()+", Color="+(playerColor?"W":"B"));

        for (int rOff : rowOffsets) {
            for (int cOff : colOffsets) {
                int newRow = row + rOff;
                int newCol = col + cOff;
                int midRow = row + rOff / 2;
                int midCol = col + cOff / 2;
                System.out.println("[DEBUG rules.hasLegalCaptureFromSquare]   Testing jump path: Mid("+midRow+","+midCol+") -> Dest("+newRow+","+newCol+")");

                if (inBounds(newRow, newCol) && inBounds(midRow, midCol)) {
                    Square landing = board.getSquare(newRow, newCol);
                    Square middle = board.getSquare(midRow, midCol);

                    boolean landingOk = landing != null && !landing.hasPiece();
                    boolean middleOk = middle != null && middle.hasPiece() && middle.getColor() != null && middle.getColor() != playerColor;

                    System.out.println("[DEBUG rules.hasLegalCaptureFromSquare]     LandingOK="+landingOk+" (Dest="+ (landing!=null?landing.hasPiece():"null")+"). MiddleOK="+middleOk+" (Mid="+ (middle!=null?middle.hasPiece():"null")+", MidColor="+ (middle!=null&&middle.getColor()!=null?(middle.getColor()?"W":"B"):"null") + ")");


                    if (landingOk && middleOk)
                    {
                        System.out.println("[DEBUG rules.hasLegalCaptureFromSquare]   Found valid capture from ("+row+","+col+") to ("+newRow+","+newCol+") over ("+midRow+","+midCol+") ");
                        return true;
                    }
                } else {
                    System.out.println("[DEBUG rules.hasLegalCaptureFromSquare]   Path out of bounds.");
                }
                }
            }
        System.out.println("[DEBUG rules.hasLegalCaptureFromSquare] No legal captures found from ("+row+","+col+")");
        return false;
    }


    // Checks if a legal capture is available from the starting square of the move.
// Returns true if at least one valid jump (capture) exists.
// Considers both regular and king pieces.
static protected boolean hasLegalCapture(Game game, Move move) {
    Square start = move.getStart(); // Get the piece's current square
    Board board = game.getBoard();
    boolean playerColor = game.getCurrentTurn().getColor(); // true = white, false = black

    int row = start.getRow();
    int col = start.getCol();

    // If king, check all 4 directions. Else, only forward diagonals based on color.
    int[][] directions = start.isKing()
        ? new int[][] { {-2, -2}, {-2, 2}, {2, -2}, {2, 2} } // King: all 4 directions
        : (playerColor
            ? new int[][] { {-2, -2}, {-2, 2} }              // White: moves upward
            : new int[][] { {2, -2}, {2, 2} });              // Black: moves downward

    for (int[] dir : directions) {
        int newRow = row + dir[0]; // landing square row
        int newCol = col + dir[1]; // landing square col

        // Skip if out of board
        if (newRow < 0 || newRow > 7 || newCol < 0 || newCol > 7)
            continue;

        Square landing = board.getSquare(newRow, newCol); // potential landing square
        if (landing != null && !landing.hasPiece()) {
            int midRow = row + dir[0] / 2; // middle square row (piece to capture)
            int midCol = col + dir[1] / 2; // middle square col
            Square middle = board.getSquare(midRow, midCol);

            // Check if middle square has opponent's piece
            if (middle != null && middle.hasPiece() && middle.getColor() != playerColor) {
                return true; // Found a legal capture move
            }
        }
    }

    return false; // No captures found
}





    //Check to see if current player can move selected piece
    //Does the color of the player match the color of the piece
    public static boolean canMovePiece(Game game, Move move)
    {
        if (game == null || move == null || move.getStart() == null) return false;

        Player currentPlayer = game.getCurrentTurn();
        Board board = game.getBoard();

        if (currentPlayer == null || board == null) return false;

        Square startSquare = board.getSquare(move.getStart().getRow(), move.getStart().getCol());

        if (startSquare == null || !startSquare.hasPiece() || startSquare.getColor() != currentPlayer.getColor()) {
            System.out.println("[DEBUG rules.canMovePiece] Fail: Invalid start square or wrong color.");
            return false;
        }

        if (!isLegal(move, game)) {
            System.out.println("[DEBUG rules.canMovePiece] Fail: Move itself is not legal.");
            return false;
        }

        boolean captureIsAvailable = isCaptureAvailableForPlayer(game);

        if (captureIsAvailable) {
            boolean chosenMoveIsCapture = isCapture(new Move(startSquare, move.getDest()), board);
            if (!chosenMoveIsCapture) {
                System.out.println("[DEBUG rules.canMovePiece] Fail: Capture available, but non-capture move chosen.");
                return false;
            }
        }
        System.out.println("[DEBUG rules.canMovePiece] OK: Move is legal and satisfies capture rule (if applicable).");
        return true;
    }

    public static ArrayList<Square> getAllPieces(Board board)
    {
        ArrayList<Square> pieces = new ArrayList<>();

        for (int i = 0; i < 8; i++) 
        {
            for (int j = 0; j < 8; j++)
            {
                Square boardSquare = board.getSquare(i, j); 
                if (boardSquare != null && boardSquare.hasPiece())
                {
                    pieces.add(boardSquare);
                }
            }
        }

        return pieces;
    }

    // recursive function for moveList
    // finds all jump chains for a given king piece
    // 4/17/2024: added color to prevent a piece from jumping over its own color
    static protected Moves kingJump(Board board, Moves moves, int rowSkip, int colSkip, Square square, boolean color)
    {
        int row = square.getRow();
        int col = square.getCol();
        int rowAdd = 1;
        int colAdd = -1;
        for(int i = 3; i > -3; i = i-2 ){
            if(i<0){
                rowAdd = -1;
            }

            if(col+colAdd < 0 || col+colAdd > 7 || row+rowAdd < 0 || row+rowAdd > 7 || (row == rowSkip && col == colSkip)){
                colAdd = colAdd*(-1);
                continue; // if the square is on any edge, break
            }

            Square temp = board.getSquare(row+rowAdd, col+colAdd);

            if(temp.hasPiece()){
                if(row+rowAdd == 0 || row+rowAdd == 7 || col+colAdd == 0 || col+colAdd == 7 
                    || (row == rowSkip && col == rowSkip) || temp.getColor() == color){
                    colAdd = colAdd*(-1);
                    continue; // if the diagonal square is on any edge, break
                }
                else{
                    temp = board.getSquare(row+(rowAdd*2), col+(colAdd*2));
                    if(!temp.hasPiece()){
                        moves.addNext(square, temp);
                        kingJump(board, moves, row+rowAdd, col+colAdd, temp, color);
                    }
                }

            colAdd = colAdd*(-1);
            }
        }
        return moves;
    }

    // recursive function for moveList
    // finds all jump chains for a given non-king piece
    // this may not work as intended depending on the behavior reguarding the moves obj. 
    static protected Moves pieceJump(Board board, Moves moves, Square square, int colorNum)
    {
        int row = square.getRow();
        int col = square.getCol();
        int rowAdd = colorNum;
        int colAdd = -1;

        for(int i = 0; i < 2; i++){
            if(i<0){
                rowAdd = -1;
            }

            if(col+colAdd < 0 || col+colAdd > 7 || row+rowAdd < 0 || row+rowAdd > 7){
                colAdd = colAdd*(-1);
                continue; // if the square is on any edge, break
            }

            Square temp = board.getSquare(row+rowAdd, col+colAdd);

            if(temp.hasPiece()){ // this will break of temp or square have a color of type NULL, so make sure that never happens
                if(row+rowAdd == 0 || row+rowAdd == 7 || col+colAdd == 0 || col+colAdd == 7 || (temp.getColor() ? -1 : 1) == colorNum){
                    colAdd = colAdd*(-1); // if the diagonal square is on any edge, or the piece is the same color, break
                    continue;
                }
                else{
                    temp = board.getSquare(row+(rowAdd*2), col+(colAdd*2));
                    if(!temp.hasPiece()){
                        moves.addNext(square, temp);
                        pieceJump(board, moves, temp, colorNum);
                    }
                }


            colAdd = colAdd*(-1);
            }
        }
        return moves;
    }

    // Generates a map of all move opportunities reguardless of color
    // This should include ALL moves, including jumps for mulipiece capture
    // 4/17/2024: fixed color logic and added logic to prevent a piece from jumping over its own color
    static protected Map<Square, Moves> moveList(Board board, boolean color)
    {
        Map<Square, Moves> moveList = new HashMap<>();
        int colorNum;
        if(color){
            colorNum = -1; // color is white
        }
        else{
            colorNum = 1; // color is black
        }

        ArrayList<Square> pieces = getAllPieces(board);

        for(int i = 0; i < pieces.size(); i++){
            Square square = pieces.get(i);
            Square squareDup = square; // just a duplicate of square for storing it in the map

            if(square.getColor() != color){
                continue; // if the color of the piece does not match the color we are generating a move list for, skip it
            }

            if(square.isKing()){ //king = true
                Moves moves = new Moves();

                for(int j = 3; j > -3; j = j - 2 ){ // this checks all diag moves starting from the upper left and moving clockwise
                    int rowAdd = 1;
                    int colAdd = -1;

                    if(j<0){
                        rowAdd = -1;
                    }
                    
                    int rowSkip = 0;
                    int colSkip = 0; // rowSkip is redundant here since the var is never updated, remove at a later time
                    int col = square.getCol();
                    int row = square.getRow();

                    if(col+colAdd < 0 || col+colAdd > 7 || row+rowAdd < 0 || row+rowAdd > 7 || (row == rowSkip && col == colSkip)){
                        colAdd = colAdd*(-1);
                        continue; // if the square is on any edge, break
                    }

                    Square temp = board.getSquare(row+rowAdd, col+colAdd);

                    if(temp.hasPiece()){
                        if(row+rowAdd == 0 || row+rowAdd == 7 || col+colAdd == 0 || col+colAdd == 7 
                            || (row == rowSkip && col == colSkip || temp.getColor() == square.getColor())){
                            colAdd = colAdd*(-1); // if the diagonal square is on any edge, or the piece is the same color, break
                            continue;
                        }
                        else{
                            temp = board.getSquare(row+(rowAdd*2), col+(colAdd*2));
                            if(!temp.hasPiece()){
                                moves.addNext(squareDup, temp);
                                kingJump(board, moves, row+rowAdd, col+colAdd, temp, color); // 
                            }
                        }
                    }
                    else{
                        moves.addNext(squareDup, temp);
                        colAdd = colAdd*(-1);
                        continue;
                    }

                    
                    colAdd = colAdd*(-1);
                    square = squareDup; // redundant (maybe)
                }

                if (moves.size() > 0) {
                    moveList.put(squareDup, moves); // if the moveList is not empty, add it to the map
                }
            }
            else{ // king = false
                Moves moves = new Moves();
                int rowAdd = colorNum;  // done so the same code can be used
                int colAdd = -1; // outside the loop so the value doesn't reset

                for(int j = 0; j < 2; j++){ // this checks all diag moves starting from the upper left and moving clockwise
                    if(j<0){
                        rowAdd = -1; // not used, remove at a later time
                    }
                    
                    int col = square.getCol();
                    int row = square.getRow();

                    if(col+colAdd < 0 || col+colAdd > 7 || row+rowAdd < 0 || row+rowAdd > 7){
                        colAdd = colAdd*(-1);
                        continue; // if the square is on any edge, break
                    }

                    Square temp = board.getSquare(row+rowAdd, col+colAdd);

                    if(temp.hasPiece()){
                        
                        if(row+rowAdd == 0 || row+rowAdd == 7 || col+colAdd == 0 || col+colAdd == 7 || temp.getColor() == square.getColor()){
                            colAdd = colAdd*(-1); // if the diagonal square is on any edge, or the piece is the same color, break
                            continue;
                        }
                        else{
                            temp = board.getSquare(row+(rowAdd*2), col+(colAdd*2));
                            if(!temp.hasPiece()){
                                moves.addNext(squareDup, temp);
                                pieceJump(board, moves, temp, colorNum);
                            }
                        }
                        
                    }
                    else{
                        moves.addNext(squareDup, temp);
                        colAdd = colAdd*(-1);
                        continue;
                    }


                    colAdd = colAdd*(-1);
                    square = squareDup; // redundant (maybe)
                }

                if (moves.size() > 0) {
                    moveList.put(squareDup, moves); // if the moveList is not empty, add it to the map
                }
            }
        }
        return moveList;
    }




    public static Moves getMovesForSquare(Board board, boolean color, int[] startSquareCoords)
    {
        Moves legalMoves = new Moves();
        int startRow = startSquareCoords[0];
        int startCol = startSquareCoords[1];

        if (!inBounds(startRow, startCol) || board == null) return legalMoves;

        Square startSquare = board.getSquare(startRow, startCol);


        if (startSquare == null || !startSquare.hasPiece() || startSquare.getColor() != color) {

            return legalMoves;
        }


        Game tempGame = new Game(color ? 101 : 102, color ? 102 : 101, color, !color, 999);
        tempGame.updateBoard(board.copy());

        while (tempGame.getCurrentTurn().getColor() != color) {
            tempGame.switchTurn();
        }

        boolean captureAvailableGlobally = isCaptureAvailableForPlayer(tempGame);



        Moves jumpMoves = new Moves();

        findPossibleJumpsRecursive(board, startSquare, startSquare, jumpMoves, new HashMap<>());


        if (captureAvailableGlobally) {


            return jumpMoves;
        } else {

            if (jumpMoves.size() == 0) {

                findPossibleNormalMoves(board, startSquare, legalMoves);
                return legalMoves;
            } else {

                System.out.println("[WARN rules.getMovesForSquare] Jumps found for ("+startRow+","+startCol+") but global capture check was false. Returning jumps.");
                return jumpMoves;
            }
        }
    }






        static private void findPossibleNormalMoves(Board board, Square start, Moves normalMoves) {
        if (board == null || start == null || normalMoves == null || !start.hasPiece()) return;

        boolean isKing = start.isKing();
        boolean color = start.getColor();
        int[] rowOffsets = isKing ? new int[]{-1, 1} : (color ? new int[]{-1} : new int[]{1});
        int[] colOffsets = {-1, 1};

        for (int rOff : rowOffsets) {
            for (int cOff : colOffsets) {
                int destRow = start.getRow() + rOff;
                int destCol = start.getCol() + cOff;

                if (inBounds(destRow, destCol)) {
                    Square dest = board.getSquare(destRow, destCol);
                    if (dest != null && !dest.hasPiece()) {
                        System.out.println("[DEBUG rules.findNormal] Adding valid normal move: ("+start.getRow()+","+start.getCol()+") -> ("+destRow+","+destCol+")");
                        normalMoves.addNext(start, dest);
                    }
                }
            }
        }
    }

    static private void findPossibleJumpsRecursive(Board board, Square originalStart, Square currentSquare, Moves jumpMoves, Map<String, Boolean> visitedInSequence) {

        if (board == null || originalStart == null || currentSquare == null || jumpMoves == null || visitedInSequence == null) {
            System.err.println("[DEBUG rules.findJumpsRec] ERROR: Null parameter passed.");
            return;
        }

        String currentKey = currentSquare.getRow() + "," + currentSquare.getCol();
        System.out.println("[DEBUG rules.findJumpsRec] ----- Entered for originalStart=("+originalStart.getRow()+","+originalStart.getCol()+"), currentSquare=(" + currentSquare.getRow() + "," + currentSquare.getCol() + ") -----");
        System.out.println("[DEBUG rules.findJumpsRec]   Visited in this sequence: " + visitedInSequence.keySet());

        if (visitedInSequence.containsKey(currentKey)) {
            System.out.println("[DEBUG rules.findJumpsRec]   Already visited (" + currentSquare.getRow() + "," + currentSquare.getCol() + ") in this sequence. Returning.");
            return;
        }
        visitedInSequence.put(currentKey, true);

        boolean isKing = currentSquare.isKing();
        if (!isKing && !currentSquare.equals(originalStart)) {
            boolean landedOnPromotionRow = (originalStart.getColor() && currentSquare.getRow() == 0) || (!originalStart.getColor() && currentSquare.getRow() == 7);
            if (landedOnPromotionRow && !originalStart.isKing()) {
                isKing = true;
                System.out.println("[DEBUG rules.findJumpsRec]   Piece at (" + currentSquare.getRow() + "," + currentSquare.getCol() + ") acts as King for next potential jumps.");
            }
    }

        boolean movingPieceColor = originalStart.getColor();
        int[] rowOffsets = isKing ? new int[]{-2, 2} : (movingPieceColor ? new int[]{-2} : new int[]{2});
        int[] colOffsets = {-2, 2};
        boolean foundFurtherJump = false;

        System.out.println("[DEBUG rules.findJumpsRec]   Checking jumps from (" + currentSquare.getRow() + "," + currentSquare.getCol() + "). King status: " + isKing + ". Color: " + (movingPieceColor ? "White" : "Black"));

        for (int rOff : rowOffsets) {
            for (int cOff : colOffsets) {
                int destRow = currentSquare.getRow() + rOff;
                int destCol = currentSquare.getCol() + cOff;
                int midRow = currentSquare.getRow() + rOff / 2;
                int midCol = currentSquare.getCol() + cOff / 2;

                System.out.println("[DEBUG rules.findJumpsRec]     Checking potential jump: (" + currentSquare.getRow() + "," + currentSquare.getCol() + ") -> Mid(" + midRow + "," + midCol + ") -> Dest(" + destRow + "," + destCol + ")");

                if (inBounds(destRow, destCol)) {
                    Square dest = board.getSquare(destRow, destCol);
                    Square middle = board.getSquare(midRow, midCol);

                    System.out.println("[DEBUG rules.findJumpsRec]       Dest square ("+destRow+","+destCol+"): " + (dest == null ? "null" : ("HasPiece=" + dest.hasPiece())));
                    System.out.println("[DEBUG rules.findJumpsRec]       Middle square ("+midRow+","+midCol+"): " + (middle == null ? "null" : ("HasPiece=" + middle.hasPiece() + ", Color=" + (middle.getColor() == null ? "null" : (middle.getColor() ? "White" : "Black")))));

                    if (dest != null && middle != null && middle.hasPiece() && middle.getColor() != movingPieceColor && !dest.hasPiece() )
                    {
                        System.out.println("[DEBUG rules.findJumpsRec]       Valid jump condition MET ");
                        foundFurtherJump = true;

                        Map<String, Boolean> nextVisited = new HashMap<>(visitedInSequence);
                            System.out.println("[DEBUG rules.findJumpsRec]       Making RECURSIVE call for jump to ("+destRow+","+destCol+"). Passing visited: " + nextVisited.keySet());
                        findPossibleJumpsRecursive(board, originalStart, dest, jumpMoves, nextVisited);
                    } else {
                            System.out.println("[DEBUG rules.findJumpsRec]       Jump condition NOT MET.");
                    }
                } else {
                        System.out.println("[DEBUG rules.findJumpsRec]     Destination (" + destRow + "," + destCol + ") is out of bounds.");
                }
            }
        }

        if (!foundFurtherJump && !currentSquare.equals(originalStart)) {
            System.out.println("[DEBUG rules.findJumpsRec]   No further jumps found from (" + currentSquare.getRow() + "," + currentSquare.getCol() + "). Checking if this move should be added.");
            boolean alreadyAdded = false;
            for(Move existingMove : jumpMoves.getMoves()) {
                if (existingMove.getStart().getRow() == originalStart.getRow() &&
                    existingMove.getStart().getCol() == originalStart.getCol() &&
                    existingMove.getDest().getRow() == currentSquare.getRow() &&
                    existingMove.getDest().getCol() == currentSquare.getCol()) {
                    alreadyAdded = true;
                        System.out.println("[DEBUG rules.findJumpsRec]     Move (" + originalStart.getRow() + "," + originalStart.getCol() + ") -> (" + currentSquare.getRow() + "," + currentSquare.getCol() + ") already present in jumpMoves.");
                    break;
                }
            }

            if (!alreadyAdded) {
                    System.out.println("[DEBUG rules.findJumpsRec]     Adding jump move to list: (" + originalStart.getRow() + "," + originalStart.getCol() + ") -> (" + currentSquare.getRow() + "," + currentSquare.getCol() + ") ");
                jumpMoves.addNext(originalStart, currentSquare);
            }
        } else if (foundFurtherJump) {
                System.out.println("[DEBUG rules.findJumpsRec]   Further jumps WERE found from (" + currentSquare.getRow() + "," + currentSquare.getCol() + "). Not adding this intermediate step as a final move.");
        } else if (currentSquare.equals(originalStart)) {
                System.out.println("[DEBUG rules.findJumpsRec]   Finished checking jumps from the original starting square (" + currentSquare.getRow() + "," + currentSquare.getCol() + ").");
        }
        System.out.println("[DEBUG rules.findJumpsRec] ----- Exiting for currentSquare=(" + currentSquare.getRow() + "," + currentSquare.getCol() + ") -----");
    }


    public static ArrayList<Square> getAllPiecesForColor(Board board, boolean color)
    {
        ArrayList<Square> pieces = new ArrayList<>();
        if (board == null) return pieces;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square boardSquare = board.getSquare(i, j);
                if (boardSquare != null && boardSquare.hasPiece() && boardSquare.getColor() != null && boardSquare.getColor() == color) {
                    pieces.add(boardSquare);
                }
            }
        }
        return pieces;
    }

}