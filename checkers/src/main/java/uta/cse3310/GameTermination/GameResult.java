package uta.cse3310.GameTermination;
import uta.cse3310.GameManager.Player;
import java.util.Map;

public class GameResult {

    //stores scores for each player
    Map<String, Integer> playerScores;

    //constructor to initialize scores
    public GameResult( Map<String, Integer> playerScores)
    {
        this.playerScores = playerScores;
    }

    //will display scores after game ends for both players.
    public void displayLeaderboard(Player player1, Player player2){

    }


}
