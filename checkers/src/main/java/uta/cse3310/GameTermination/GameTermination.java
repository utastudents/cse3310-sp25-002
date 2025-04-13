package uta.cse3310.GameTermination;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GamePlay.GamePlay;
import java.util.Map;

public class GameTermination {


    private boolean gameOver = false; // Flag to indicate if the game is over, game state
    private String finalWinner = null; // Stores the winner's ID or "Draw"

    // Method to handle the end of the game and declare the winner
    public void endGame(Map<String, Integer> playerScores, String winningPlayer) {
        if (gameOver) {
            System.out.println("The game has already ended."); //to handle abrupt endings
            return;
        }
        // Mark the game as over
        gameOver = true;

        // Check if there's a winner or a draw
        if (winningPlayer != null) {
            // If a winning player is provided, declare the winner
            System.out.println("Game Over! " + winningPlayer + " has won!");
        } else {
            // If no winner, it's a draw
            System.out.println("Game Over! It's a draw!");
        }
}
    // This method will save the results to the database
    public void saveResultsToDatabase(Map<String, Integer> playerScores) {
        // Placeholder for database logic
        System.out.println("Saving results to database...");
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
        // Logic for handling game restart (this could include resetting states or notifying other components)
        System.out.println("Game restart requested.");
    }
}
