package uta.cse3310.GamePlay;

import java.util.LinkedList;

import uta.cse3310.GameManager.Board;


//class rules checks if the move is legal
public class rules
{
    //checks if the move is within the bounds of the board
    //8x8 board
    //issue (maybe): class Move needs to be visible to get move set
    static protected boolean inBounds(LinkedList moves)
    {
        int rowSize = 8, colSize = 8;
        return false; //Default
    }

    //checks if the piece moves diagonally up-right and up-left
    //for king: moves diagonally down-right and down-left in addition to above
    static protected boolean isDiagonal(boolean isKing, LinkedList moves, Board board)
    {
        return false; //Default
    }

    //checks if the square being moved to is occupied by a piece
    static protected boolean occupied(LinkedList moves, Board board)
    {
        return false; //Default
    }

    //checks how many spots moved up to compared to number of pieces
    //0 pieces = 1, 1 piece = 2, 2 pieces = 4 etc...
    static protected boolean pieceToMoves(LinkedList moves, Board board)
    {
        return false; //Default
    }
}