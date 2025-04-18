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
        rules rule = new rules();
        
        // Default is return NULL as it assumes the move is illegal until proven legal
        Board updatedBoard = null;
        Board currentGameBoard = game.getBoard();
        Move currentMove = moves.getFirst();
        Square currentSquare = currentMove.getStart();
        Square destinationSquare = currentMove.getDest();
        Player activePlayer = game.getCurrentTurn();
        int numMoves = moves.size();
        int counter = 0;

        // Loop to go through each move in the linked list
        while(counter < numMoves)
        {
            // Check if there is at least one legal move the player can make with this piece
            if(rule.canMovePiece(currentGameBoard, currentSquare, game))
            {
                if(rule.isLegal(currentMove, game))
                {
                    if(activePlayer.getColor())
                    {
                        if(destinationSquare.getRow() == 0)
                        {
                            currentGameBoard.execute(currentMove, true);
                        }
                        else
                        {
                            currentGameBoard.execute(currentMove, false);
                        }
                    }
                    else
                    {
                        if(destinationSquare.getRow() == 7)
                        {
                            currentGameBoard.execute(currentMove, true);
                        }
                        else
                        {
                            currentGameBoard.execute(currentMove, false);
                        }
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

    public Map<Square, Moves> returnMoves(Game game)
    {
        Player currentPlayer = game.getCurrentTurn();
        rules rule = new rules();
        Map<Square, Moves> moveList = rule.moveList(game.getBoard(), currentPlayer.getColor());
        return moveList;
    }
}