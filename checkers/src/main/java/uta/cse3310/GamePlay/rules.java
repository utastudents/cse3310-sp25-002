package uta.cse3310.GamePlay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Player;


//class rules checks if the move is legal
public class rules
{   
    //checks if the move is within the bounds of the board
    //8x8 board
    //assumption: program starts counting at 0
    //assumption: gameManager waits until current move is processed
    //and then processes the next move
    static protected boolean inBounds(Moves moves)
    {
        int index = moves.size() - 1;
        Square square = moves.getDest(index);

        //assuming program starts counting at 0
        if (square.getCol() > 7 || square.getCol() < 0 ||
            square.getRow() > 7 || square.getRow() < 0)
            return false;
        return true;
    }

    //checks if the piece moves diagonally up-right and up-left
    //for king: moves diagonally down-right and down-left in addition to above
    static protected boolean isDiagonal(LinkedList<Moves> moves, Board board)
    {
        /*if(isKing = true)
        {
            allow the king to move forward/backwards along the diagonal
        }
        else{
            the piece should not be allowed to move down-left or down-right
        }*/
        return false; //Default
    }


    //checks if the square being moved to is occupied by a piece
    static protected boolean occupied(Moves moves, Board board) {
        if (moves == null || moves.size() == 0) {
            return false;
        }
    
        int lastIdx = moves.size() - 1;
        Square dest = moves.getDest(lastIdx);
    
        int row = dest.getRow();
        int col = dest.getCol();
    
        Square boardSquare = board.getSquare(row, col);
    
        if (boardSquare == null) {
            return false;
        }
    
        return boardSquare.hasPiece(); // returns true only if a piece exists on that square
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

    //will call occupied to check if a square is occupied by another piece, then check
    //if the user can check that piece
    static protected boolean canCapture(LinkedList<Moves> moves, Square square, int playerId, Player player1, Player player2, boolean playerColor)
    {
        //if the space is occupied and the following square is free you can capture that piece and move to the next free space
        int currentPlayer;

        if(player1.getPlayerId() == playerId) //checks to see if player 1 is currently playing
        {
            currentPlayer = playerId;
            playerColor = player1.getColor(); //see what color is assigned to the current player
        }
        else if(player2.getPlayerId() == playerId) //checks to see if player 2 is currently playing
        {
            currentPlayer = playerId;
            playerColor = player2.getColor(); //see what color is assigned to the current player
        }
        //false is the square is unoccupied or the square is occupied by the one of the players own pieces
        if(!square.hasPiece() || (square.hasPiece() && square.getColor() == playerColor))
        {
            return false;
        }
        return true;//default
    }



    //Check to see if current player can move selected piece
    //Does the color of the player match the color of the piece
    static protected boolean canMovePiece(int row, int col, Board board, int playerId, Player player1, Player player2, boolean CurrentPlayerColor)
    {
        Square boardSquare = board.getSquare(row, col); // is there something on the current square
        boolean boardColor = boardSquare.getColor(); //finds the color of the piece trying to be moved

        // checks to see if there is no piece on the current square or if the player's id doesn't match up
        if(!boardSquare.hasPiece()|| player1.getPlayerId() != playerId|| player2.getPlayerId()!= playerId)
        {
            return false;
        }

        //is it currently player 1's turn or player 2's turn
        if(player1.getPlayerId() == playerId)
        {
            CurrentPlayerColor = player1.getColor() ;//player 1 can only move with its associated color
        }
        else if(player2.getPlayerId() == playerId)
        {
            CurrentPlayerColor = player2.getColor() ;//player 2 can only move with its associated color
        }

        if(boardColor != CurrentPlayerColor) //if the piece trying to be moved doesn't belong to the player trying to move it
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
                if (boardSquare != null) 
                {
                    pieces.add(boardSquare);
                }
            }
        }

        return pieces;
    }

     // find out if this needs to report a piece as 'being captured'
    static protected boolean removeCaptured(LinkedList<Moves> moves, Board board){
    boolean capture = false;
    for(Moves order : moves)
    {
        for (int i = 0; i < order.size(); i++) {
            Square starting = order.getStart(i);
            Square ending = order.getDest(i);

            int RowDiff = Math.abs(starting.getRow() - ending.getRow());
            int ColDiff = Math.abs(starting.getCol() - ending.getCol());

            if (RowDiff == 2 && ColDiff == 2)
            {
                int RowCap = (starting.getRow() + ending.getRow()) / 2 ;
                int ColCap = (starting.getCol() + ending.getCol()) / 2 ;

                Square SquareCap = board.getSquare(RowCap, ColCap);

                if(SquareCap.hasPiece())
                {
                    SquareCap.remove();
                    capture = true;
                }
            }
        }
    }
    return capture;
}


    // recursive function for moveList
    // finds all jump chains for a given king piece

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
