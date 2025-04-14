package uta.cse3310.GameTermination;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GamePlay.GamePlay;
import java.util.Map;

public class GameTermination {


    private boolean gameOver = false; // Flag to indicate if the game is over, game state
    private String finalWinner = null; // Stores the winner's ID or "Draw"

    // Method to handle the end of the game and declare the winner
    public void endGame(Map<String, Integer> playerScores, int winningPlayer) {
        if (gameOver) {
            System.out.println("The game has already ended."); //to handle abrupt endings
            return;
        }
        // Mark the game as over
        gameOver = true;

        // Check if there's a winner or a draw
       if (winningPlayer != null) {
            finalWinner = winningPlayer;
            System.out.println("Game Over! " + winningPlayer + " has won!");
        } else {
            finalWinner = "Draw";
            System.out.println("Game Over! It's a draw!");
        }
    }
    // This method will save the results to the database
    public void saveResultsToDatabase(Map<String, Integer> playerScores) {
        System.out.println("Saving results to database...");
        System.out.println("Final winner: " + finalWinner);
        System.out.println("Scores: " + playerScores);
    }

    // This method will confirm if the results were successfully stored in the database
    public void confirmDataStored() {
        // Placeholder for confirmation logic
        System.out.println("Results confirmed as stored in the database.");
    }

    // Sends a final termination message to gameplay once a match is over
    public String sendResults() {
        return "results";
    }

    // New method to handle game restart requests
    public void gameRestartReq() {
       gameOver = false;
        finalWinner = null;
        System.out.println("Game restart requested. Game state has been reset.");
    }
    // Returns the winner or draw
    public String getWinner() {
        return finalWinner;
    }
}
