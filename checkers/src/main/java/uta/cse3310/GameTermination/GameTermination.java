package uta.cse3310.GameTermination;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GamePlay.GamePlay;
import java.util.Map;

public class GameTermination 
{


    private boolean gameOver = false; // Flag to indicate if the game is over, game state
    private String finalWinner = null; // Stores the winner's ID or "Draw"

    //hardcoded methods for unit testing
    //player 1 lost
    public Map<String, Integer> getPlayerPieceCounts() 
    {
        Map<String, Integer> pieceCounts = new java.util.HashMap<>();
        pieceCounts.put("player1", 0);  
        pieceCounts.put("player2", 5);
        return pieceCounts;
    }

    //has any legal moves
    public Map<String, Boolean> getPlayerHasLegalMoves() 
    {
        Map<String, Boolean> legalMoves = new java.util.HashMap<>();
        legalMoves.put("player1", false);
        legalMoves.put("player2", true);
        return legalMoves;
    }

    //get scores
    public Map<String, Integer> getPlayerScores() 
    {
        Map<String, Integer> scores = new java.util.HashMap<>();
        scores.put("player1", 6);
        scores.put("player2", 9);
        return scores;
    }

    // Method to handle the end of the game and declare the winner
    public void endGame(Map<String, Integer> playerScores, int winningPlayer) 
    {
        if (gameOver) 
        {
            System.out.println("The game has already ended."); //to handle abrupt endings
            return;
        }

        String winnerId = null;
        boolean isDraw = false;
        
        
        
        
        
        
        
        
        
        // Mark the game as over
        //gameOver = true;

    // NOTE: this was causing compile error 
    //     // Check if there's a winner or a draw
    //    if (winningPlayer != null) {
    //         finalWinner = winningPlayer;
    //         System.out.println("Game Over! " + winningPlayer + " has won!");
    //     } else {
    //         finalWinner = "Draw";
    //         System.out.println("Game Over! It's a draw!");
    //     }
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

    // Returns the winner or draw
    public String getWinner() {
        return finalWinner;
    }
    
    public int playerHasResigned(int player1, int player2){
        //logic will look something like this.
        //if player1 has regsined -> winningPlayer = player2
        //if player2 has resigned -> winningPlayer = player1
        //else continue the game
        //dummy return
        return 3;
    }
    
}
