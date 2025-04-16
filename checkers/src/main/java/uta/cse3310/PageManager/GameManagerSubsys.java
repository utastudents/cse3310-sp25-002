package uta.cse3310.PageManager;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.GamePageController;

public class GameManagerSubsys {

    private GamePageController gamePageController;

    public GameManagerSubsys(GamePageController controller) {
        this.gamePageController = controller;
    }

    public game_status getGameInfo(int playerID) {
       game_status status = new game_status();
       
       try {

            Game game = gamePageController.returnGame(playerID);
            Board board = gamePageController.sendBoard(playerID);

            if (game == null || board == null) {
                status.msg = "Game not found for player " + playerID;
                status.gameOver = true;
                return status;
            }

        status.turn = gamePageController.playerTurn(playerID);
        status.winner = gamePageController.getWinner(playerID);
        status.loser = gamePageController.getLoser(playerID);
        status.draw = gamePageController.getDraw(playerID);
        status.gameOver =  (status.winner != null || status.draw);
       } catch (Exception e) {

        status.msg = "Error retrieving game info: " + e.getMessage();
        status.gameOver = true;
        
       }

       return status;
   }
    
     
}
