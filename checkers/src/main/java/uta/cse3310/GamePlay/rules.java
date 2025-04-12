package uta.cse3310.GamePlay;

import java.util.ArrayList;
import java.util.LinkedList;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;

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
    static protected boolean occupied(LinkedList<Moves> moves, Board board) {
        // Safety check: make sure the moves list isn't empty
        if (moves == null || moves.isEmpty()) {
            return false;
        }
    
        // Get the latest Moves object (a group of individual Move steps)
        Moves lastMoves = moves.getLast();
    
        // Get the index of the last move within that Moves group
        int lastIdx = lastMoves.size() - 1;
    
        // Extra safety: make sure there's at least one move in the group
        if (lastIdx < 0) {
            return false;
        }
    
        // Get the destination square of the last move
        Square dest = lastMoves.getDest(lastIdx);
    
        // Get the row and column of that square
        int row = dest.getRow();
        int col = dest.getCol();
    
        // Use the board to check if something exists at that location
        Square boardSquare = board.getSquare(row, col);
    
        // If the board square is not null, it's occupied
        return boardSquare != null;
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
            else if (yDirection > 0)
                ++y;
            else if (yDirection < 0)
                --y;
            /*
            //for exception handling
            else
                throw new RuntimeException("error in pieceToMoves");
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
    static protected boolean canCapture(LinkedList<Moves> moves, Board board)
    {
     

        //call occupied, check if the square is occupied
        //if the space is occupied and the following square is free you can capture that piece and move to the next free space
        //return a map showing were the player can move

        return false;//default
    }

    //Check to see if current player can move selected piece
    //Does the color of the player match the color of the piece
    static protected boolean canMovePiece(Game game)
    {
        return true; //Default value
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

    // removes captured pieces from the board 
    // find out if this needs to report a piece as 'being captured'
    static protected boolean removeCaptured(LinkedList<Moves> moves, Board board)
    {
        return false;
    }
}
