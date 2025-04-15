package uta.cse3310.PageManager;
import uta.cse3310.GameManager.GamePageController;

public class GameManagerSubsys {

    private GamePageController gamePageController;
    int playerID;

    //public GameManagerSubsys(GamePageController controller) {
        //this.gamePageController = controller;
    //}
        //will be updating the methods once GamepageController updates
    //public void setPlayerID(int id) {
        //this.playerID = id;
        //gamePageController.setPlayerID(id); sends player id to gameManager
   // }
   // public game_status getGameInfo() {
   //    game_status status = new game_status();
   //}

    /**
     * Hardcoded version — simulates what we'd get from GamePageController( when it is implemented) and GameManager.
     */
    public GameManagerSubsys()  {

    }

    public game_status getGameInfo() {
        game_status status = new game_status();

    // fake values 
      try {
        status.turn = 1;
        status.score = 10.0f;
        status.gameOver = false;
        status.gameID = 456;
        status.winner = null;
        status.loser = null;
        status.msg = "This is the hardcoded verison";
        } catch (Exception e) {
        status.msg = "Error (hardcoded): " + e.getMessage();
        status.gameOver = true;
     }
        return status; 
    }
}
