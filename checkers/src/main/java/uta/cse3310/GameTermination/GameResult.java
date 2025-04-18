package uta.cse3310.GameTermination;
import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.PairUp.Match;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameResult {

    // Stores scores for each player by their ID
    private Map<Integer, Integer> playerScores;

    private Integer winningPlayerId;

    // Stores all game results
    private List<GameResult> gameHistory = new ArrayList<>();

    // Constructor to initialize scores
    public GameResult(Map<Integer, Integer> playerScores) {
        this.playerScores = playerScores;
        this.winningPlayerId = null; //no winner yet
    }

    public Map<Integer, Integer> getPlayerScores() {
        return playerScores;
    }

    public Integer getWinningPlayerId() {
        return winningPlayerId;
    }

    public boolean isDraw() {
        return winningPlayerId == null;
    }

    public List<Integer> getPlayerIds() {
        return new ArrayList<>(playerScores.keySet());
    }

    //to call player's id anywhere later
    public void setWinner(Player player) {
        this.winningPlayerId = player.getPlayerId();
    }
    //match is drawn
    public void setDraw() {
        this.winningPlayerId = null;
    }

    public void trackCapturedPieces(Player player, Board board, Match match) {
    int totalPiecesAtStart = 12;
    int remainingPieces = 0;

    Boolean playerColor = null;

    if (player.getPlayerId() == match.getPlayer1Id()) {
        playerColor = match.getPlayer1Color();
    } else if (player.getPlayerId() == match.getPlayer2Id()) {
        playerColor = match.getPlayer2Color();
    } else {
        System.out.println("Player not part of this match.");
        return;
    }

    // Loop through board and count pieces of the player's color
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            Square square = board.getSquare(i, j);
            if (square != null && square.getColor() != null && square.getColor().equals(playerColor)) {
                remainingPieces++;
            }
        }
    }

    int capturedPieces = totalPiecesAtStart - remainingPieces;

    if (capturedPieces >= 12) {
        System.out.println("Player " + player.getPlayerId() + " (" + (playerColor ? "White" : "Black") + ") has lost!");
        GameTermination gameTermination = new GameTermination();
        gameTermination.checkGameOver();
    }
}

// This method checks if a player has any legal moves left.
// If there are none, the player loses.
    public void checkForLegalMoves(Player player, Game game) {
        GamePlay gameplay = new GamePlay();
        Map<Square, Moves> legalMoves = gameplay.returnMoves(game);

        if (legalMoves == null || legalMoves.isEmpty()) {
        System.out.println("Player " + player.getPlayerId() + " has no legal moves and loses!");
        GameTermination gameTermination = new GameTermination();
        gameTermination.checkGameOver();
    }
}

    // This method updates the current score of a player after a move.
    public void updateScores(Player player, int score) {
        int id = player.getPlayerId();
        playerScores.put(id, score);
    }

    // This is for unit testing support to verify the scores updates.
    public int getScore(int playerId) {
        return playerScores.getOrDefault(playerId, -1);
    }

    //used for testing
    @Override
    public String toString()
    {
        if (isDraw())
        {
            return "Game ended in a draw.\nScores: " + playerScores.toString();
        }
        else
        {
            return "Winner: " + winningPlayerId + "\nScores: " + playerScores.toString();
        }
    }
}
