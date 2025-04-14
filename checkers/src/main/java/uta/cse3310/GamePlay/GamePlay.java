package uta.cse3310.GamePlay;

import java.util.HashMap;
import java.util.Map;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;

public class GamePlay
{
    public Board returnBoard(Game game, Moves moves)
    {
        // Default is return NULL as it assumes the move is illegal until proven legal
        Board updatedBoard = null;
        Board currentGameBoard = game.getBoard();
        Move currentMove = moves.getFirst();
        Square currentSquare = currentMove.getStart();
        int numMoves = moves.size();
        int counter = 0;

        // First check if the player is allowed to move the current piece
        while(counter < numMoves)
        {
            /*if(canMovePiece(currentGameBoard, currentSquare, game))
            {

            }
            else
            {
                break;
            }*/

        }
        
        
        return updatedBoard;
    }

    public Map<Square, Move> returnMoves(Game game)
    {
        Map<Square, Move> moveList = new HashMap<>();
        
        return moveList;
    }
}