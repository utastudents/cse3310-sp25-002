package uta.cse3310.GamePlay;

import java.util.LinkedList;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;

//class rules checks if the move is legal
public class rules
{
    //checks if the move is within the bounds of the board
    //8x8 board
    //issue (maybe): class Move needs to be visible to get move set
    static protected boolean inBounds(LinkedList<Moves> moves)
    {
        int rowSize = 8, colSize = 8;
       // bd9659   if((moves <=0 || moves> rowSize)||(moves<=0 || moves>colSize))
       return false; //Default
    }

    //checks if the piece moves diagonally up-right and up-left
    //for king: moves diagonally down-right and down-left in addition to above
    static protected boolean isDiagonal(boolean isKing, LinkedList<Moves> moves, Board board)
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

    //it may also be helpful to include something in this method to let the player know if
    //they can capture the piece occupieing the square
    //checks if the square being moved to is occupied by a piece
    static protected boolean occupied(LinkedList<Moves> moves, Board board)
    {
        return false; //Default
    }

    //checks how many spots moved up to compared to number of pieces
    //0 pieces = 1, 1 piece = 2, 2 pieces = 4 etc...
    static protected boolean pieceToMoves(LinkedList<Moves> moves, Board board)
    {
        return false; //Default
    }
}
