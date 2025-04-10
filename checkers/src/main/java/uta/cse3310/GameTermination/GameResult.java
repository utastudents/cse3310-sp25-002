package uta.cse3310.GameTermination;

import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GameManager.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameResult {

    // Stores scores for each player by their ID
    private Map<String, Integer> playerScores;

    // Stores all game results
    private List<GameResult> gameHistory = new ArrayList<>();

    // Constructor to initialize scores
    public GameResult(Map<String, Integer> playerScores) {
        this.playerScores = playerScores;
    }

    // This method will update the number of pieces captured for each player.
    // If a player has all 12 pieces captured, they lose.
    public void trackCapturedPieces(Player player) {
        //int capturedPieces = player.getCapturedCount(); // assuming GamePlay provides this
        int capturedPieces = 0; // Placeholder until integration with GamePlay

        if (capturedPieces >= 12) {
            // Final declaration of loss; actual game-end logic should be handled by GameManager or GamePlay
            System.out.println("Player " + player.getPlayerId() + " has lost!");
        }
    }

    // This method checks if a player has any legal moves left.
    // If there are none, the player loses.
    public void checkForLegalMoves(Player player) {
        //boolean hasLegalMoves = player.hasLegalMoves(); // assuming GamePlay provides this
        boolean hasLegalMoves = true; // Placeholder until integrated

        if (!hasLegalMoves) {
            System.out.println("Player " + player.getPlayerId() + " has lost!");
        }
    }

    // This method updates the current score of a player after a move.
    public void updateScores(Player player, int score) {
        String id = String.valueOf(player.getPlayerId());
        playerScores.put(id, score);
    }
}

