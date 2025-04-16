package uta.cse3310.GamePlay;

import java.util.ArrayList;
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
    static protected boolean inBounds(Move move)
    {
        Square square = move.getDest();

        if (square.getCol() > 7 || square.getCol() < 0 ||
            square.getRow() > 7 || square.getRow() < 0)
            return false;
        return true;
    }

    //checks if a move is legal based on checkers rules
    //returns true if:
    // - the destination square is unoccupied AND
    // - the move is either a regular 1-step diagonal move
    // - OR a 2-step jump over an opponent's piece
    //returns false in all other cases
    // During testing, make methods for repetetive lines to reduce file length
    static protected boolean isLegal(Move move, Game game) {
        boolean legality = false;
        Square start = move.getStart(); // starting square
        Square dest = move.getDest();   // destination square

        // check if move is out of bounds or destination is occupied
        if (!(dest == null || !inBounds(move) || dest.hasPiece()))
        {
            Board board = game.getBoard();
            Player player = game.getCurrentTurn();
            boolean playerColor = player.getColor();
            int rowDiff = Math.abs(dest.getRow() - start.getRow());
            int colDiff = Math.abs(dest.getCol() - start.getCol());

            if(playerColor)
            {
                // isLegal for white pieces
                // King piece
                if(start.isKing())
                {
                    // Regular move
                    if (rowDiff == 1 && colDiff == 1)
                    {
                        legality = true;
                        game.lastCapture();
                    }
                    // Capture piece
                    else if (rowDiff == 2 && colDiff == 2)
                    {
                        int middleRow = start.getRow();
                        int middleCol = start.getCol();

                        // Determine if the move is to the left or right
                        if(middleCol < dest.getCol())
                        {
                            middleCol++;
                        }
                        else
                        {
                            middleCol--;
                        }

                        // Determine if the move is up or down
                        if(middleRow < dest.getRow())
                        {
                            middleRow++;
                        }
                        else
                        {
                            middleRow--;
                        }

                        Square middleSquare = board.getSquare(middleRow, middleCol);

                        // middle square must exist, have a piece, and be of the opposite color
                        if (middleSquare != null && middleSquare.hasPiece() && middleSquare.getColor() != playerColor)
                        {
                            legality = true;
                        }
                    }
                }
                else
                {
                    // Regular piece, not king
                    if(start.getRow() > dest.getRow())
                    {
                        // Regular move
                        if (rowDiff == 1 && colDiff == 1)
                        {
                            legality = true;
                            game.lastCapture();
                        }
                        // Capture piece
                        else if (rowDiff == 2 && colDiff == 2)
                        {
                            int middleRow = start.getRow() + 1;
                            int middleCol = start.getCol();

                            // Determine if the move is to the left or right
                            if(middleCol < dest.getCol())
                            {
                                middleCol++;
                            }
                            else
                            {
                                middleCol--;
                            }

                            Square middleSquare = board.getSquare(middleRow, middleCol);

                            // Middle square must exist, have a piece, and be of the opposite color
                            if (middleSquare != null && middleSquare.hasPiece() && middleSquare.getColor() != playerColor)
                            {
                                legality = true;
                            }
                        }
                    }
                }
            }
            else
            {
                // isLegal for black pieces
                if(start.isKing())
                {
                    // Regular move
                    if (rowDiff == 1 && colDiff == 1)
                    {
                        legality = true;
                    }
                    // Capture piece
                    else if (rowDiff == 2 && colDiff == 2)
                    {
                        int middleRow = start.getRow();
                        int middleCol = start.getCol();

                        // Determine if the move is to the left or right
                        if(middleCol < dest.getCol())
                        {
                            middleCol++;
                        }
                        else
                        {
                            middleCol--;
                        }

                        // Determine if the move is up or down
                        if(middleRow < dest.getRow())
                        {
                            middleRow++;
                        }
                        else
                        {
                            middleRow--;
                        }

                        Square middleSquare = board.getSquare(middleRow, middleCol);

                        // middle square must exist, have a piece, and be of the opposite color
                        if (middleSquare != null && middleSquare.hasPiece() && middleSquare.getColor() != playerColor)
                        {
                            legality = true;
                        }
                    }
                }
                else
                {
                    if(start.getRow() < dest.getRow())
                    {
                        // Regular move
                        if (rowDiff == 1 && colDiff == 1)
                        {
                            legality = true;
                        }
                        // Capture piece
                        else if (rowDiff == 2 && colDiff == 2)
                        {
                            int middleRow = start.getRow() + 1;
                            int middleCol = start.getCol();

                            // Determine if the move is to the left or right
                            if(middleCol < dest.getCol())
                            {
                                middleCol++;
                            }
                            else
                            {
                                middleCol--;
                            }

                            Square middleSquare = board.getSquare(middleRow, middleCol);

                            // Middle square must exist, have a piece, and be of the opposite color
                            if (middleSquare != null && middleSquare.hasPiece() && middleSquare.getColor() != playerColor)
                            {
                                legality = true;
                            }
                        }
                    }
                }
            }
        }

        // Any other move is illegal
        return legality;
    }

    //checks how many spots moved up to compared to number of pieces
    //0 pieces = 1, 1 piece = 2, 2 pieces = 4 etc...
    static protected boolean pieceToMoves(Moves moves, Board board)
    {
        int index = moves.size() - 1;
        Square squareDest = moves.getDest(index);
        Square squareStart = moves.getStart(index);

        int dist = Math.abs(squareDest.getRow() - squareStart.getRow());

        //if negative, piece moved left, if positive, piece moved right
        int xDirection = squareDest.getCol() - squareStart.getCol();
        //if negative, piece moved down, if positive, piece moved up
        int yDirection = squareDest.getRow() - squareStart.getRow();
        
        if (dist == 1)
            return true;

        int x = 0, y = 0;
        int numPieces = 0;
        //find pieces on the way to the destination
        for (int i = dist; i > 0; --i) 
        {
            if (xDirection > 0)
                ++x;
            else if (xDirection < 0)
                --x;
            /*
            //for exception handling
            else
                throw new RuntimeException("piece did not move\n");
            */

            if (yDirection > 0)
                ++y;
            else if (yDirection < 0)
                --y;
            /*
            //for exception handling
            else
                throw new RuntimeException("piece did not move\n");
            */

            Square currSquare = board.getSquare(squareStart.getCol() + x, squareStart.getRow() + y);
            if (currSquare.hasPiece())
                ++numPieces;

        }
        
        //if 0 pieces != 1 dist, 1 piece != 2 dist, 2 pieces != 4 dist etc...
        //return false
        if (dist / numPieces != 2)
            return false;

        return true;
    }

    static protected boolean canCapture(Game game, Move move)
    {
        boolean capturable = false;

        Square startSquare = move.getStart();
        Square destinationSquare = move.getDest();

        int rowDistance = Math.abs(startSquare.getRow() - destinationSquare.getRow());
        int columnDistance = Math.abs(startSquare.getCol() - destinationSquare.getCol());

        boolean isKing = startSquare.isKing();

        if(isKing)
        {
            if(rowDistance == 2)
            {

            }
            else if(columnDistance == 2)
            {

            }
        }

        return capturable;
    }

    //Check to see if current player can move selected piece
    //Does the color of the player match the color of the piece
    static protected boolean canMovePiece(Board board, Square square, Game game)
    {
        // is there something on the current square
        boolean boardColor = square.getColor(); //finds the color of the piece trying to be moved

        Player currentPlayer = game.getCurrentTurn(); //who's turn is it
        boolean currentPlayerColor = currentPlayer.getColor(); //what color is associated with that player

        // checks to see if there is no piece on the current square or if the player's assigned color doesn't the peice they're trying to move
        if(!square.hasPiece()|| boardColor != currentPlayerColor)
        {
            return false;
        }

        Moves canJump = new Moves();
        if(square.isKing()) //checks to see if there is anyway for the player to jump legally
        {
            canJump = rules.kingJump(board, new Moves(), -1, -1, square);
        }
        else
        {
            canJump = rules.pieceJump(board, new Moves(), square, currentPlayerColor ? 1: -1);
        }
        if(canJump.size() <= 0)// return false if the player can't jump anything even if its a king
        {
            return false;
        }
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
    // this may not work as intended depending on the behavior reguarding the moves obj. 
    static protected Moves kingJump(Board board, Moves moves, int rowSkip, int colSkip, Square square)
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
                if(row+rowAdd == 0 || row+rowAdd == 7 || col+colAdd == 0 || col+colAdd == 7 || (row == rowSkip && col == rowSkip)){
                    colAdd = colAdd*(-1);
                    continue;
                }
                else{
                    temp = board.getSquare(row+(rowAdd*2), col+(colAdd*2));
                    if(!temp.hasPiece()){
                        moves.addNext(square, temp);
                        kingJump(board, moves, row+rowAdd, col+colAdd, temp);
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

            if(temp.hasPiece()){
                if(row+rowAdd == 0 || row+rowAdd == 7 || col+colAdd == 0 || col+colAdd == 7){
                    colAdd = colAdd*(-1);
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
    static protected Map<Square, Moves> moveList(Board board, boolean color)
    {
        Map<Square, Moves> moveList = new HashMap<>();
        int colorNum;
        if(color){
            colorNum = 1; // color is white
        }
        else{
            colorNum = -1; // color is black
        }
        //black on top; black == false
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
                        if(row+rowAdd == 0 || row+rowAdd == 7 || col+colAdd == 0 || col+colAdd == 7 || (row == rowSkip && col == colSkip)){
                            colAdd = colAdd*(-1);
                            continue;
                        }
                        else{
                            temp = board.getSquare(row+(rowAdd*2), col+(colAdd*2));
                            if(!temp.hasPiece()){
                                moves.addNext(squareDup, temp);
                                kingJump(board, moves, row+rowAdd, col+colAdd, temp);
                                //square = temp;
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
                moveList.put(squareDup, moves);
            }
            else{ // king = false
                Moves moves = new Moves();
                int rowAdd = colorNum;  // done so the same code can be used
                int colAdd = -1; // outside the loop so the value doesn't reset

                for(int j = 0; j < 2; j++){ // this checks all diag moves starting from the upper left and moving clockwise
                    if(j<0){
                        rowAdd = -1;
                    }
                    
                    int col = square.getCol();
                    int row = square.getRow();

                    if(col+colAdd < 0 || col+colAdd > 7 || row+rowAdd < 0 || row+rowAdd > 7){
                        colAdd = colAdd*(-1);
                        continue; // if the square is on any edge, break
                    }

                    Square temp = board.getSquare(row+rowAdd, col+colAdd);

                    if(temp.hasPiece()){
                        if(row+rowAdd == 0 || row+rowAdd == 7 || col+colAdd == 0 || col+colAdd == 7){
                            colAdd = colAdd*(-1);
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
                moveList.put(squareDup, moves);
            }
        }
        return moveList;
    }
}
