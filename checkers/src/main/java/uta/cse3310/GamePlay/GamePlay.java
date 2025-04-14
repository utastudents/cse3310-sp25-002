package uta.cse3310.GamePlay;

import java.util.HashMap;
import java.util.Map;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GamePlay.rules;

public class GamePlay
{
    public Board returnBoard(Game game, Moves moves)
    {
        // Default is return NULL as it assumes the move is illegal until proven legal
        Board updatedBoard = null;
        Board currentGameBoard = game.getBoard();
        Move currentMove = moves.getFirst();
        Square currentSquare = currentMove.getStart();
        Square destinationSquare = currentMove.getDest();
        Player activePlayer = game.getCurrentTurn();
        int numMoves = moves.size();
        int counter = 0;
        rules rule = new rules();

        // First check if the player is allowed to move the current piece
        while(counter < numMoves)
        {
            if(rule.canMovePiece(currentGameBoard, currentSquare, game))
            {


                if(activePlayer.getColor())
                {
                    if(destinationSquare.getRow() == 7)
                    {

                    }
                    else
                    {

                    }
                }
                else
                {
                    if(destinationSquare.getRow() == 0)
                    {

                    }
                    else
                    {

                    }
                }

            }
            else
            {
                break;
            }

            // Counter to make sure all moves are processed
            counter++;
            currentMove = moves.getNext(currentMove);
        }
        
        
        return updatedBoard;
    }

    public Map<Square, Move> returnMoves(Game game)
    {
        Map<Square, Move> moveList = new HashMap<>();
        
        return moveList;
    }
}