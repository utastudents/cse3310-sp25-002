package uta.cse3310.GameTermination;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GamePlay.GamePlay;
import java.util.Map;
import java.util.HashMap;
import uta.cse3310.DB.DB;
public class GameTermination
{

    private boolean gameOver = false; // Flag to indicate if the game is over, game state
    private String finalWinner = null; // Stores the winner's ID or "Draw"

    //hardcoded methods for unit testing
    //player 1 lost
    public Map<Integer, Integer> getPlayerPieceCounts()
    {
        Map<Integer, Integer> pieceCounts = new HashMap<>();
        pieceCounts.put(1, 0);
        pieceCounts.put(2, 5);
        return pieceCounts;
    }

    //has any legal moves
    public Map<Integer, Boolean> getPlayerHasLegalMoves()
    {
        Map<Integer, Boolean> legalMoves = new HashMap<>();
        legalMoves.put(1, false);
        legalMoves.put(2, true);
        return legalMoves;
    }

    //get scores
    public Map<Integer, Integer> getPlayerScores()
    {
        Map<Integer, Integer> scores = new HashMap<>();
        scores.put(1, 6);
        scores.put(2, 9);
        return scores;
    }

    public void checkGameOver()
    {
        Map<Integer, Integer> pieceCounts = getPlayerPieceCounts();     //temp till real
        Map<Integer, Boolean> legalMoves = getPlayerHasLegalMoves();    //temp till real
        Map<Integer, Integer> playerScores = getPlayerScores();         //temp till real

        Integer winnerId = null;
        boolean isDraw = false;

        for (Map.Entry<Integer, Integer> entry : pieceCounts.entrySet())
        {
            if (entry.getValue() == 0)
            {
                for (Integer otherPlayer : pieceCounts.keySet())
                {
                    if (!otherPlayer.equals(entry.getKey()))
                    {
                        winnerId = otherPlayer;
                        break;
                    }
                }
                break;
            }
        }

        if (winnerId == null && legalMoves.values().stream().noneMatch(v -> v))
        {
            isDraw = true;
        }

        if (winnerId != null || isDraw)
        {
            endGame(playerScores, isDraw ? -1 : winnerId);
        }

    }

    // Method to handle the end of the game and declare the winner
    public void endGame(Map<Integer, Integer> playerScores, int winningPlayer)
    {
        if (gameOver)
        {
            System.out.println("The game has already ended."); //to handle abrupt endings
            return;
        }

        // Mark the game as over
        gameOver = true;

        if (winningPlayer != -1)
        {
            finalWinner = "Player " + winningPlayer;
            System.out.println("Game Over! Player " + winningPlayer + " has won");
        }
        else
        {
            finalWinner = "Draw";
            System.out.println("Game Over! It's a draw!");
        }

        saveResultsToDatabase(playerScores);
        System.out.println("LOAD_SUMMARY_SCREEN");

    }

    // This method will save the results to the database
    public void saveResultsToDatabase(Map<Integer, Integer> playerScores) {
        System.out.println("Saving results to database...");
        System.out.println("Final winner: " + finalWinner);
        System.out.println("Scores: " + playerScores);

        for (Map.Entry<Integer, Integer> entry : playerScores.entrySet()) {
            Integer playerId = entry.getKey();   
            Integer score = entry.getValue();     

            String username = "Player" + playerId;  
            DB.updatePlayer(username, score);

            System.out.println("The database is updated" + username + " = " + score);
        }
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

    public boolean isGameOver() { //getter for endGame unit test
        return gameOver;
    }

}

