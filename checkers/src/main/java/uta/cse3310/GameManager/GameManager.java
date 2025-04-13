package uta.cse3310.GameManager;

// Importing necessary classes and packages
import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.Bot.BotI.BotI;
import uta.cse3310.Bot.BotII.BotII;

import java.util.ArrayList;

public class GameManager {

    GamePlay gp;
    GameTermination gt;
    BotI b1;
    BotII b2;
    // Maximum number of games that can run concurrently
    private static final int Maximum_GAMES = 15;
    private ArrayList<Game> games;

    // Constructor to initialize components
    public GameManager() {
        gp = new GamePlay();
        gt = new GameTermination(); // Handles game ending logic


        // Initialize the list of games
        games = new ArrayList<>(Maximum_GAMES);
        for (int i = 0; i < Maximum_GAMES; i++) {
            games.add(null); // initialize slots with null
        }
    }

    // Checking if there is any slots available to create a new game for two players.
    public boolean createGame(int player1id, int player2id, boolean player1color, boolean player2color) {
        int availableSlot = getAvailableGameSlot();
        if (availableSlot == -1) {
            System.out.println("No available game slots.");
            return false;
        }
        // Create a new game with two players (IDs and their color), and assign to available slot
        //Game newGame = new Game(player1id, player2id, player1color, player2color, availableSlot);
        //games.set(availableSlot, newGame);

        System.out.println("Game created in slot " + availableSlot + " between Player " + player1id + " and Player " + player2id);
        return true;
    }

    // Check for first available slot in games list
    private int getAvailableGameSlot() {
        for (int i = 0; i < Maximum_GAMES; i++) {
            if (games.get(i) == null || !games.get(i).gameActive()) {
                return i;
            }
        }
        return -1;
    }

    // End a game manually and clear it
    public void terminateGame(int gameNumber) {
        if (gameNumber >= 0 && gameNumber < Maximum_GAMES && games.get(gameNumber) != null) {
            // gt.endGame(games.get(gameNumber)); // yet to determine GameTermination logic:
            //They dont have this but I believe having something to directly terminate the game is not a bad idea.
            games.set(gameNumber, null);
            System.out.println("Game " + gameNumber + " has been terminated.");
        }
    }

    public void restartGame(int gameNumber) {
        if (gameNumber >= 0 && gameNumber < Maximum_GAMES && games.get(gameNumber) != null) {

            Game currentGame = games.get(gameNumber);

            // Retrieve the players from the current game
            /*
            Player player1 = currentGame.getCurrentTurn(); // could also use getPlayer1()
            Player player2 = (player1 == currentGame.getPlayer1()) ? currentGame.getPlayer2()
                    : currentGame.getPlayer1(); */

            // Notify GameTermination about the restart
            gt.gameRestartReq();

            // Re-add players to the matchmaking queue
           /* PairUp.addPlayerToQueue(player1);
            PairUp.addPlayerToQueue(player2); */

            System.out.println("Game " + gameNumber + " restart requested. Players moved back to the queue.");
        } else {
            System.out.println("Invalid game number or game is null. Restart request ignored.");
        }
    }

    // Check if any slot is free
    public boolean hasAvailableSlot() {
        return getAvailableGameSlot() != -1;
    }

    // Gets list of current games
    public ArrayList<Game> getGames() {
        return games;
    }
}
