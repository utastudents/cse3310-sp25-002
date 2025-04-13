package uta.cse3310.GameTermination;
import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GameManager.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameResult {

    // Stores scores for each player by their ID
    private Map<String, Integer> playerScores;
    
    private String winningPlayerId;

    // Stores all game results
    private List<GameResult> gameHistory = new ArrayList<>();

    // Constructor to initialize scores
    public GameResult(Map<String, Integer> playerScores) {
        this.playerScores = playerScores;
        this.winningPlayerId = null; //no winner yet
    }

    public Map<String, Integer> getPlayerScores() {
        return playerScores;
    }

    public String getWinningPlayerId() {
        return winningPlayerId;
    }

    public boolean isDraw() {
        return winningPlayerId == null;
    }
    //to call player's id anywhere later
    public void setWinner(Player player) {
        this.winningPlayerId = String.valueOf(player.getPlayerId());
    }

    // This method will update the number of pieces captured for each player.
    // If a player has all 12 pieces captured, they lose.
    public void trackCapturedPieces(Player player) {
        //int capturedPieces = player.getCapturedCount(); // assuming GamePlay provides this
        int capturedPieces = 0; // Placeholder until integration with GamePlay

        if (capturedPieces >= 12) {
            // Remember to implement actual game ending logic inside GameTermination
            System.out.println("Player " + player.getPlayerId() + " has lost!");
      // Remember to implement actual game ending logic inside GameTermination here
        }
    }

    // This method checks if a player has any legal moves left.
    // If there are none, the player loses.
     public void checkForLegalMoves(Player player1, Player player2) {
        //boolean player1HasLegalMoves = player1.hasLegalMoves(); // Assuming GamePlay provides this
        //boolean player2HasLegalMoves = player2.hasLegalMoves(); // Assuming GamePlay provides this

        //if (!player1HasLegalMoves && !player2HasLegalMoves) {
            // If both players have no legal moves left, it's a draw
          //  System.out.println("Both players have no legal moves left. It's a draw!");
            // End the game with a draw
           // endGame(null); // No winner in a draw
       // } else if (!player1HasLegalMoves) {
            // Player 1 has no legal moves left, Player 2 wins
         //   System.out.println("Player " + player1.getPlayerId() + " has lost due to no legal moves.");
          //  endGame(player2); // Player 2 is the winner
       // } else if (!player2HasLegalMoves) {
            // Player 2 has no legal moves left, Player 1 wins
         //   System.out.println("Player " + player2.getPlayerId() + " has lost due to no legal moves.");
          //  endGame(player1); // Player 1 is the winner
        }

    // This method updates the current score of a player after a move.
    public void updateScores(Player player, int score) {
        String id = String.valueOf(player.getPlayerId());
        playerScores.put(id, score);
    }

    // This is for unit testing support to verify the scores updates.
    public int getScore(String playerId) {
        return playerScores.getOrDefault(playerId, -1);
    }
}