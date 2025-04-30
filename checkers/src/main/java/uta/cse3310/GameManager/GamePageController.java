package uta.cse3310.GameManager;

import java.util.Map;
import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GamePlay.rules;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GamePageController {
    private GameManager gameManager;
    private GamePlay gp;


    public GamePageController(GameManager gameManager){
        this.gameManager = gameManager;
        this.gp = new GamePlay();
    }



public List<List<Integer>> getMovablePieces(int playerID) {
        List<List<Integer>> movablePiecesCoords = new ArrayList<>();
        Game game = gameManager.findGameByPlayerId(playerID);

        if (game == null || !game.gameActive() || game.getCurrentTurn() == null) {
            System.err.println("[ERROR GamePageController] getMovablePieces: Game not found, inactive, or turn is null for player " + playerID);
            return movablePiecesCoords;
        }

        Player currentPlayer = game.getCurrentTurn();
        if (currentPlayer.getPlayerId() != playerID) {
            System.out.println("[INFO GamePageController] getMovablePieces: Not player " + playerID + "'s turn.");
            return movablePiecesCoords;
        }

        Board board = game.getBoard();
        boolean playerColor = currentPlayer.getColor();

        boolean captureIsMandatory = rules.isCaptureAvailableForPlayer(game);
        List<Square> piecesToConsider = new ArrayList<>();

        if (captureIsMandatory) {
            System.out.println("[DEBUG GamePageController] getMovablePieces: Capture is mandatory for player " + playerID);
            ArrayList<Square> allPlayerPieces = rules.getAllPiecesForColor(board, playerColor);
            for (Square piece : allPlayerPieces) {
                if (rules.isCaptureAvailableFromSquare(game, piece)) {
                    piecesToConsider.add(piece);
                    System.out.println("[DEBUG GamePageController] getMovablePieces: Found mandatory capture piece at [" + piece.getRow() + "," + piece.getCol() + "]");
                }
            }
        } else {
            System.out.println("[DEBUG GamePageController] getMovablePieces: No mandatory capture for player " + playerID);
            piecesToConsider = rules.getAllPiecesForColor(board, playerColor);
        }


        for (Square piece : piecesToConsider) {
            if (piece == null) continue;
            int[] coords = {piece.getRow(), piece.getCol()};
            Moves pieceMoves = rules.getMovesForSquare(board, playerColor, coords);
            if (pieceMoves != null && pieceMoves.size() > 0) {
                System.out.println("[DEBUG GamePageController] getMovablePieces: Piece at [" + piece.getRow() + "," + piece.getCol() + "] has " + pieceMoves.size() + " valid moves.");
                movablePiecesCoords.add(List.of(piece.getRow(), piece.getCol()));
            } else {
                System.out.println("[DEBUG GamePageController] getMovablePieces: Piece at [" + piece.getRow() + "," + piece.getCol() + "] has NO valid moves in current context.");
            }
        }

        System.out.println("[DEBUG GamePageController] getMovablePieces: Returning movable pieces for player " + playerID + ": " + movablePiecesCoords);
        return movablePiecesCoords;
    }



    //sending the board to page controller
    public Board sendBoard(int playerID){
        Game game = gameManager.findGameByPlayerId(playerID);
        if(game == null){return null;}
        return game.getBoard();
    }

    //returns the game called by the searched player id
    public Game returnGame(int playerID){
        return gameManager.findGameByPlayerId(playerID);
    }

    //returns the moves list of the player
    public Moves processMove(int playerID, Move move){
        Moves moves = new Moves();
        moves.addNext(move);
        gameManager.processMove(playerID, move);
        return moves; 
    }

    //returns possible moves for a specific square
    public Map<Square, Moves> getAllowedMoves(int playerID, int[] square){
        Game game = gameManager.findGameByPlayerId(playerID);
        GamePlay gp = new GamePlay();
        if(game == null){return null;}
        return gp.getMovesForSquare(game, square);
    }

    //returns who's turn it is
    public int playerTurn(int playerID){
        Game game = gameManager.findGameByPlayerId(playerID);
        int turn = game.getCurrentTurn().getPlayerId();
        return turn;
    }

    //returns the winner
    public Integer getWinner(int playerID){
        Game game = gameManager.findGameByPlayerId(playerID);
        if(game == null){return null;}
        if(game.getWinner() == null){return null;}
        return game.getWinner().getPlayerId();
    }

    //grab the loser of the game
    public Integer getLoser(int playerID){
        Game game = gameManager.findGameByPlayerId(playerID);
        if(game == null){return null;}
        if(game.getWinner() == null){return null;}
        return game.getLoser().getPlayerId();
    }

    //returns that game ended in a draw
    public Boolean getDraw(int playerID){
        Game game = gameManager.findGameByPlayerId(playerID);
        GamePlay gp = new GamePlay();
        Map<Square, Moves> moveList = gp.returnMoves(game);

        if(moveList.isEmpty() == true || game.getMovesSinceLastCapture() == 40){
            game.GameDeclareDraw();
            return true;
        }
        return false;
    }

    //grab both player ids
    public int[] getAllPlayerIDs(int playerID){
        Game game = gameManager.findGameByPlayerId(playerID);
        if(game == null){return null;}
        int[] ids = new int[2];
        ids[0] = game.getPlayer1ID();
        ids[1] = game.getPlayer2ID();
        return ids;
    }

}