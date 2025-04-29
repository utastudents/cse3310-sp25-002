package uta.cse3310.GameManager;


import uta.cse3310.GamePlay.rules;
import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.PairUp.Match;
import uta.cse3310.PairUp.PairUp; // For re-adding players to matchmaking
import uta.cse3310.Bot.BotI.BotI;
import uta.cse3310.Bot.BotII.BotII;
import uta.cse3310.GameManager.Move;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private static final int MAXIMUM_GAMES = 10;
    private static ArrayList<Game> games;
    private GamePlay gp;
    private GameTermination gt;
    GamePageController ggc;
    GamePairController grc;

    // Constructor to initialize components
    public GameManager(){
        gp = new GamePlay();
        gt = new GameTermination();
        grc = new GamePairController();
        if (games == null) {
            initializeGames();
        }
    }

    public void initializeGames(){
        games = new ArrayList<>(MAXIMUM_GAMES);
        for (int i = 0; i < MAXIMUM_GAMES; ++i){
            games.add(null);
        }
    }
    // Create a new game with two players
    public Game createGame(Match match){
        int availableSlot = getAvailableGameSlot();
        if(availableSlot == -1){
            System.out.println("No available game slots.");
            return null;
        }
        Game newGame = grc.newMatch(match);
        games.set(availableSlot, newGame);
        
        return newGame;
    }

    // Find first available game slot
    private int getAvailableGameSlot(){
        for (int i = 0; i < MAXIMUM_GAMES; i++){
            if(games.get(i) == null || !games.get(i).gameActive()){ // gameActive() is handled by Game.java
                return i;
            }
        }
        return -1;
    }

    // Process a move from a player
    public void processMove(int playerId, Move move){
        Game game = findGameByPlayerId(playerId);
        if(game != null && game.gameActive()){
            boolean success = gp.processAndExecuteMove(game, move);
            if (success) {
                Player winner = game.getWinner();
                boolean draw = game.checkDrawCondition();

                if (winner != null || draw) {
                    System.out.println("Game " + game.gameNumber() + " ended after move by " + playerId);
                } else {
                    game.switchTurn();
                    System.out.println("Turn switched to player: " + game.getCurrentTurn().getPlayerId());
                }
            } else {
                System.out.println("Invalid move attempted by player " + playerId);
            }
        } else {
            System.out.println("Invalid move request: game not found or inactive for player " + playerId);
        }
    }

   //Terminate a game and notify GameTermination
    public void terminateGame(int gameNumber){
        if(gameNumber >= 0 && gameNumber < MAXIMUM_GAMES && games.get(gameNumber) != null){
            Game game = games.get(gameNumber);

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
    }

    public Moves requestBotMoves(Game game, int botId){
        if (game == null || game.getCurrentTurn() == null || game.getCurrentTurn().getPlayerId() != botId) {
            System.err.println("[ERROR GameManager] Bot move requested by ID " + botId + " but it's not their turn or game is null.");
            return null;
        }

        boolean botColor = game.getCurrentTurn().getColor();

        if(botId == 0){
            BotI bot = new BotI();
            bot.setColor(botColor);
            return bot.requestMove(game.getBoard());
        } else if(botId == 1){
            BotII bot = new BotII();
            bot.setColor(botColor);
            return bot.requestMove(game.getBoard());
        }
        System.err.println("[ERROR GameManager] Unknown bot ID requested: " + botId);
        return null;
    }

    public void progressGame(int gameNumber){
        if (gameNumber < 0 || gameNumber >= games.size() || games.get(gameNumber) == null) {
            System.err.println("[ERROR GameManager] progressGame called with invalid game number: " + gameNumber);
            return;
        }
        Game game = games.get(gameNumber);

        if(game == null || !game.gameActive()){
            System.out.println("[DEBUG GameManager] progressGame: Game " + gameNumber + " is null or inactive.");
            return;
        }

        Player currentPlayer = game.getCurrentTurn();
        if (currentPlayer == null) {
            System.err.println("[ERROR GameManager] progressGame: Current turn player is null for game " + gameNumber);
            return;
        }
        int playerId = currentPlayer.getPlayerId();

        boolean isBot = (playerId == 0 || playerId == 1);

        if(isBot){
            System.out.println("[DEBUG GameManager] progressGame: Bot's turn (Player " + playerId + ") in game " + gameNumber);
            Moves botMoves = requestBotMoves(game, playerId);

            if(botMoves != null && botMoves.size() > 0){
                boolean moveMade = false;
                for (Move botMove : botMoves.getMoves()) {
                    if (gp.processAndExecuteMove(game, botMove)) {
                        System.out.println("[DEBUG GameManager] Bot " + playerId + " move executed successfully in game " + gameNumber);
                        moveMade = true;

                        Player winner = game.getWinner();
                        boolean draw = game.checkDrawCondition();
                        if (winner != null || draw) {
                            System.out.println("Game " + game.gameNumber() + " ended after bot move.");
                        } else {
                            game.switchTurn();
                            System.out.println("Turn switched to player: " + game.getCurrentTurn().getPlayerId());
                        }
                        break;
                    } else {
                        System.out.println("[WARN GameManager] Bot " + playerId + " move failed validation/execution. Trying next.");
                    }
                }
                if (!moveMade) {
                    System.err.println("[ERROR GameManager] Bot " + playerId + " failed to make any valid move from its list in game " + gameNumber);
                    if (!game.isDraw()) game.checkDrawCondition();
                    if (!game.isDraw() && game.gameActive()) {
                        System.err.println("Forcing draw for game " + gameNumber + " as bot could not move.");
                        game.GameDeclareDraw();
                    }
                }
        } else {
            System.out.println("[WARN GameManager] Bot " + playerId + " provided no moves for game " + gameNumber);
            if (!game.isDraw()) game.checkDrawCondition();
            if (!game.isDraw() && game.gameActive()) {
                System.err.println("Forcing draw for game " + gameNumber + " as bot provided no moves.");
                game.GameDeclareDraw();
            }
        }
    } else {
        System.out.println("[DEBUG GameManager] progressGame: Human's turn (Player " + playerId + ") in game " + gameNumber + ". Waiting for input.");
    }
    }


    // Find a game by player ID
    public Game findGameByPlayerId(int playerId){
        for (Game game : games){
            if(game != null){
                if(game.getPlayer1ID()==playerId || game.getPlayer2ID()==playerId){
                    return game;
                }
            }
        }
        return null;
    }

    public Game findGameById(int gameId) {
        for (Game game : games) {
            if (game != null && game.gameNumber() == gameId) {
                return game;
            }
        }
        return null;
    }


    // Check if any slot is free
    public boolean hasAvailableSlot(){
        return getAvailableGameSlot() != -1;
    }

    // Returns all games (used by frontend or controller)
    public ArrayList<Game> getGames(){
        return games;
    }

    // Check if a player is in any active game
    public boolean isPlayerInGame(int playerId){
        return findGameByPlayerId(playerId) != null;
    }

    // Count active games
    public int getActiveGameCount(){
        int count = 0;
        for (Game game : games){
            if(game != null && game.gameActive()){
                count++;
            }
        }
        return count;
    }

    // Remove a player from a game (e.g., on disconnect)
    public void removePlayer(int playerId){
        Game game = findGameByPlayerId(playerId);
        if(game != null){
            if(game.getPlayer1ID() == playerId){
                game.Player1Quit(); // Implemented in Game.java
            } else {
                game.Player2Quit();
            }
        }
    }

   //Player requests to quit and ends game
    public void playerQuit(int playerId){
        Game game = findGameByPlayerId(playerId); // Local method
        if(game == null){
            System.out.println("Player " + playerId + " is not in any active game.");
            return;
        }
        int player1Id = game.getPlayer1ID();
        int player2Id = game.getPlayer2ID();
        int player1Score = game.getPlayer1Score();
        int player2Score = game.getPlayer2Score();

        if(player1Id == playerId){
            game.Player1Quit(); // Game.java
            System.out.println("Player " + playerId + "(Player 1) quit.");
            
            
        } else {
            game.Player2Quit(); // Game.java
            System.out.println("Player " + playerId + "(Player 2) quit.");
        }

        Map<Integer, Integer> playerScores = new HashMap<>();
        playerScores.put(player1Id, player1Score);
        playerScores.put(player2Id, player2Score);

        int winningPlayerId = (player1Id == playerId) ? player2Id : player1Id;
        gt.endGame(playerScores, winningPlayerId);
        games.set(game.gameNumber(), null); // gameNumber() in Game.java
        System.out.println("Player " + playerId + " quit. Game " + game.gameNumber() + " ended.");

    }
}
