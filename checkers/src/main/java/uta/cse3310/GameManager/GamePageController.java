package uta.cse3310.GameManager;

import java.util.Map;
import uta.cse3310.GamePlay.GamePlay;

public class GamePageController {
    private GameManager gameManager;

    GamePageController(GameManager gameManager){
        this.gameManager = gameManager;
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

    //returns possible moves 
    public Map<Square, Moves> getAllowedMoves(int playerID, int[] square){
        Game game = gameManager.findGameByPlayerId(playerID);
        GamePlay gp = new GamePlay();
        if(game == null){return null;}
        return gp.returnMoves(game);
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

        if(moveList.isEmpty() == true || game.lastCapture() == 40){
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