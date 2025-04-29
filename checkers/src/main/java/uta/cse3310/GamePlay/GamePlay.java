package uta.cse3310.GamePlay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Player;
import java.util.Arrays;



public class GamePlay
{


    public boolean processAndExecuteMove(Game game, Move move)
    {
        if (game == null || move == null || move.getStart() == null || move.getDest() == null) {
            System.err.println("[ERROR GamePlay.processAndExecuteMove] Invalid arguments (null game or move details).");
            return false;
        }

        Board currentGameBoard = game.getBoard();
        Player activePlayer = game.getCurrentTurn();

        if (currentGameBoard == null || activePlayer == null) {
            System.err.println("[ERROR GamePlay.processAndExecuteMove] Invalid game state (null board or player).");
            return false;
        }

        if (rules.canMovePiece(game, move))
        {
            boolean isCapture = rules.isCapture(move, currentGameBoard);

            currentGameBoard.execute(move, isCapture);

            if (isCapture) {
                game.newCapture();
            } else {
                game.incrementMoveCounter();
            }

            System.out.println("[DEBUG GamePlay.processAndExecuteMove] Board after move:\n" + currentGameBoard.toString());
            return true;
        }
        else
        {
            System.out.println("[WARN GamePlay.processAndExecuteMove] Move deemed illegal by rules.canMovePiece. Move rejected.");
            System.out.println("[WARN GamePlay.processAndExecuteMove] Board state remains unchanged:\n" + currentGameBoard.toString());
            return false;
        }
    }



    public Map<Square, Moves> getMovesForSquare(Game game, int[] targetSquareCoords)
    {
        if (game == null || targetSquareCoords == null || targetSquareCoords.length != 2) {
            return new HashMap<>();
        }
        Player currentPlayer = game.getCurrentTurn();
            if (currentPlayer == null) return new HashMap<>();

        Moves movesForPiece = rules.getMovesForSquare(game.getBoard(), currentPlayer.getColor(), targetSquareCoords);

        Map<Square, Moves> moveMap = new HashMap<>();
        if (movesForPiece != null && movesForPiece.size() > 0) {
            Square startSquare = game.getBoard().getSquare(targetSquareCoords[0], targetSquareCoords[1]);
            if (startSquare != null) {
                moveMap.put(startSquare, movesForPiece);
            } else {
                System.err.println("[ERROR GamePlay.getMovesForSquare] Could not retrieve start square from board for coords: " + Arrays.toString(targetSquareCoords));
            }
        }
        return moveMap;
    }



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

        // Loop to go through each move in the linked list
        while(counter < numMoves)
        {
            // Check if there is at least one legal move the player can make with this piece
            if(rules.canMovePiece(game, currentMove))
            {
                if(rules.isLegal(currentMove, game))
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
        if (game == null) return new HashMap<>();

        Player currentPlayer = game.getCurrentTurn();
        Board board = game.getBoard();

        if (currentPlayer == null || board == null) return new HashMap<>();

        Map<Square, Moves> allPossibleMoves = new HashMap<>();
        ArrayList<Square> playerPieces = rules.getAllPiecesForColor(board, currentPlayer.getColor());

        for (Square piece : playerPieces) {
            if (piece == null) continue;
            int[] coords = {piece.getRow(), piece.getCol()};
            Moves pieceMoves = rules.getMovesForSquare(board, currentPlayer.getColor(), coords);
            if (pieceMoves != null && pieceMoves.size() > 0) {
                allPossibleMoves.put(piece, pieceMoves);
            }
        }
        return allPossibleMoves;
    }
}