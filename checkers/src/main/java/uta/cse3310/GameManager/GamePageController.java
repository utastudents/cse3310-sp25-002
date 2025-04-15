package uta.cse3310.GameManager;

public class GamePageController {
    private GameManager gameManager;

    GamePageController(GameManager gameManager){
        this.gameManager = gameManager;
    }
    public Board sendBoard(int playerID){
        Game game = gameManager.findGameByPlayerId(playerID);
        if(game == null){return null;}
        return game.getBoard();
    }

    public Game returnGame(int playerID){
        return gameManager.findGameByPlayerId(playerID);
    }

    public Moves receiveMoves(Move move, int playerID){
        Moves moves = new Moves();
        moves.addNext(move);
        gameManager.processMove(playerID, move);
        return moves; 
    }

    public int playerTurn(int playerID){
        Game game = gameManager.findGameByPlayerId(playerID);
        int turn = game.getCurrentTurn().getPlayerId();
        return turn;
    }
    public Integer isThereWinner(int playerID){
        Game game = gameManager.findGameByPlayerId(playerID);
        if(game == null){return null;}
        if(game.getWinner() == null){return null;}
        return game.getWinner().getPlayerId();
    }
}