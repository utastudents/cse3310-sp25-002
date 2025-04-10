package uta.cse3310.GameTermination;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GamePlay.GamePlay;
import java.util.Map;

public class GameTermination {

    // Method to handle the end of the game and declare the winner
    public void endGame(Map<String, Integer> playerScores, String winningPlayer) {
        // Print the game over message and declare the winner
        System.out.println("Game Over! " + winningPlayer + " has won!");

        // Save final results to the database
        saveResultsToDatabase(playerScores);
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
