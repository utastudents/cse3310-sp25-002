package uta.cse3310.GameTermination;
import uta.cse3310.GameManager.Player;
import java.util.LinkedList;
import java.util.Queue;

public class GameTermination {

    //Queue to rotate players
    Queue<Player> playerQueue;

    public GameTermination() {
        playerQueue = new LinkedList<>();

    }
    
    //sends a request for game restart and players wait in queue.
    public void gameRestartReq(){

    }

    //saves results to the database after the game ends that will aid in creating the leaderboard.
    public void saveResults(Player player1, Player player2){

    }

    //sends a final termination message to gameplay once a match is over
    public String sendResults(){
        return "results";
    }


}
