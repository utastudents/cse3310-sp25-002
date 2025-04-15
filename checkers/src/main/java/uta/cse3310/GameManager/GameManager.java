package uta.cse3310.GameManager;

import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.PairUp.PairUp; // For re-adding players to matchmaking
import uta.cse3310.GameManager.Move;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private static final int MAXIMUM_GAMES = 10;
    private ArrayList<Game> games;
    private GamePlay gp;
    private GameTermination gt;

    // Constructor to initialize components
    public GameManager() {
        games = new ArrayList<>(MAXIMUM_GAMES);
        for (int i = 0; i < MAXIMUM_GAMES; i++) {
            games.add(null);
        }
        gp = new GamePlay(); // Handles game rules and moves
        gt = new GameTermination(); // Handles game-ending logic
    }

    // Create a new game with two players
    public boolean createGame(int player1id, int player2id, boolean player1color, boolean player2color) {
        int availableSlot = getAvailableGameSlot();
        if (availableSlot == -1) {
            System.out.println("No available game slots.");
            return false;
        }
        Game newGame = new Game(player1id, player2id, player1color, player2color, availableSlot);
        games.set(availableSlot, newGame);
        System.out.println(
                "Game created in slot " + availableSlot + " between Player " + player1id + " and Player " + player2id);
        return true;
    }

    // Find first available game slot
    private int getAvailableGameSlot() {
        for (int i = 0; i < MAXIMUM_GAMES; i++) {
            if (games.get(i) == null || !games.get(i).gameActive()) { // gameActive() is handled by Game.java
                return i;
            }
        }
        return -1;
    }

    // Process a move from a player
    public void processMove(int playerId, Move move) {
        Game game = findGameByPlayerId(playerId);
        if (game != null && game.gameActive()) {
            Moves moves = new Moves();
            moves.addNext(move);
            gp.returnBoard(game, moves); // from GamePlay.java
        } else {
            System.out.println("Invalid move: game not found or inactive.");
        }
    }
    /*Note: The large commented-out code sections are fully implemented. 
    However, there are data type mismatches with the method signatures. 
    I have requested my groupmate to update the relevant data types accordingly.
    Once those changes are made, these sections will be uncommented and used as intended.
    */
    
    /* Terminate a game and notify GameTermination
    public void terminateGame(int gameNumber) {
        if (gameNumber >= 0 && gameNumber < MAXIMUM_GAMES && games.get(gameNumber) != null) {
            gt.endGame(games.get(gameNumber)); // Implement in GameTermination.java

            int player1Id = game.getPlayer1ID();
            int player2Id = game.getPlayer2ID();
            int player1Score = game.getPlayer1Score();
            int player2Score = game.getPlayer2Score();

            Map<Integer, Integer> playerScores = new HashMap<>();
            playerScores.put(player1Id, player1Score);
            playerScores.put(player2Id, player2Score);

            int winningPlayerId = (player1Score > player2Score) ? player1Id
                    : (player2Score > player1Score) ? player2Id : -1;

            gt.endGame(playerScores, winningPlayerId);
            games.set(gameNumber, null);
            System.out.println("Game " + gameNumber + " has been terminated.");
        }
    } */


    // Find a game by player ID
    public Game findGameByPlayerId(int playerId) {
        for (Game game : games) {
            if (game != null){
                if (game.getPlayer1ID()==playerId || game.getPlayer2ID()==playerId){
                    return game;
                }
            }
        }
        return null;
    }

    // Check if any slot is free
    public boolean hasAvailableSlot() {
        return getAvailableGameSlot() != -1;
    }

    // Returns all games (used by frontend or controller)
    public ArrayList<Game> getGames() {
        return games;
    }

    // Check if a player is in any active game
    public boolean isPlayerInGame(int playerId) {
        return findGameByPlayerId(playerId) != null;
    }

    // Count active games
    public int getActiveGameCount() {
        int count = 0;
        for (Game game : games) {
            if (game != null && game.gameActive()) {
                count++;
            }
        }
        return count;
    }

    // Remove a player from a game (e.g., on disconnect)
    public void removePlayer(int playerId) {
        Game game = findGameByPlayerId(playerId);
        if (game != null) {
            if (game.getPlayer1ID() == playerId) {
                game.Player1Quit(); // Implemented in Game.java
            } else {
                game.Player2Quit();
            }
        }
    }

    /* Player requests to quit and ends game
    public void playerQuit(int playerId) {
        Game game = findGameByPlayerId(playerId); // Local method
        if (game == null) {
            System.out.println("Player " + playerId + " is not in any active game.");
            return;
        }

        if (game.getPlayer1ID() == playerId) {
            game.Player1Quit(); // Game.java

            int player1Id = game.getPlayer1ID();
            int player2Id = game.getPlayer2ID();
            int player1Score = game.getPlayer1Score();
            int player2Score = game.getPlayer2Score();

            Map<Integer, Integer> playerScores = new HashMap<>();
            playerScores.put(player1Id, player1Score);
            playerScores.put(player2Id, player2Score);
            gt.endGame(playerScores, player2Id);
        } else {
            game.Player2Quit(); // Game.java
            int player2Id = game.getPlayer2ID();
            int player1Id = game.getPlayer2Id();
            int player1Score = game.getPlayer1Score();
            int player2Score = game.getPlayer2Score();

            Map<Integer, Integer> playerScores = new HashMap<>();
            playerScores.put(player1Id, player1Score);
            playerScores.put(player2Id, player2Score);
            gt.endGame(playerScores, player1Id);
        }

        games.set(game.gameNumber(), null); // gameNumber() in Game.java
        System.out.println("Player " + playerId + " quit. Game " + game.gameNumber() + " ended.");

    }*/
}
