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


public class GamePlay
{


    public boolean processAndExecuteMove(Game game, Move move)
    {
        if (game == null || move == null || move.getStart() == null || move.getDest() == null) {
            System.err.println("[ERROR GamePlay.processAndExecuteMove] Invalid arguments (null game or move details).");
            return false;
        }

        Board currentGameBoard = game.getBoard();
        game.lockBoard();
        try {
        Player activePlayer = game.getCurrentTurn();

        if (currentGameBoard == null || activePlayer == null) {
            System.err.println("[ERROR GamePlay.processAndExecuteMove] Invalid game state (null board or player).");
            return false;
        }

        if (rules.canMovePiece(game, move))
        {
            boolean isCapture = rules.isCapture(move, currentGameBoard);

            System.out.println("[DEBUG isCapture] isCapture value before execute(): " + isCapture);
            if (isCapture) {
                game.newCapture();
                currentGameBoard.execute(move, isCapture);
            } else {
                game.incrementMoveCounter();
                currentGameBoard.execute(move, isCapture);
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
        } finally {
            game.unlockBoard();
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