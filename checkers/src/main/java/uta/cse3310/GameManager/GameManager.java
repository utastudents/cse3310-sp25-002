package uta.cse3310.GameManager;

import java.util.Collection;
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
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {

    private Map<Integer, Game> activeGames;
    private GamePlay gp;
    private GameTermination gt;
    GamePageController ggc;
    GamePairController grc;

    // Constructor to initialize components
    public GameManager(){
        gp = new GamePlay();
        gt = new GameTermination();
        grc = new GamePairController();
        activeGames = new HashMap<>();
    }


    public Game createGame(Match match){
        Game newGame = grc.newMatch(match);
        if (newGame != null) {
            activeGames.put(newGame.gameNumber(), newGame);
            System.out.println("Game created and added to map with ID: " + newGame.gameNumber());
        } else {
            System.err.println("Failed to create a new game from match.");
            return null;
        }
        return newGame;
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
        Game game = activeGames.get(gameNumber);
        if(game != null){

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
            activeGames.remove(gameNumber);
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
        Game game = activeGames.get(gameNumber);

        if (game == null) {
            System.err.println("[ERROR GameManager] progressGame called with invalid or terminated game number: " + gameNumber);
            return;
        }

        if(!game.gameActive()){
            System.out.println("[DEBUG GameManager] progressGame: Game " + gameNumber + " is inactive. Removing if present.");
            activeGames.remove(gameNumber);
            return;
        }
        Player currentPlayer = game.getCurrentTurn();
        if (currentPlayer == null) {
            System.err.println("[ERROR GameManager] progressGame: Current turn player is null for game " + gameNumber);
            terminateGame(gameNumber);
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
                terminateGame(gameNumber);
            }
        }
    } else {
        System.out.println("[DEBUG GameManager] progressGame: Human's turn (Player " + playerId + ") in game " + gameNumber + ". Waiting for input.");
    }
    }


    public Game findGameByPlayerId(int playerId){
        for (Game game : activeGames.values()){
            if(game != null && game.gameActive()){
                if(game.getPlayer1ID() == playerId || game.getPlayer2ID() == playerId){
                    return game;
                }
            }
        }
        return null;
    }

    public Game findGameById(int gameId) {
        return activeGames.get(gameId);
    }




    public Collection<Game> getGames(){
        return activeGames.values();
    }

    // Check if a player is in any active game
    public boolean isPlayerInGame(int playerId){
        return findGameByPlayerId(playerId) != null;
    }

    // Count active games
    public int getActiveGameCount(){
        int count = 0;
        for (Game game : activeGames.values()){
            if(game != null && game.gameActive()){
                count++;
            }
        }
        return count;
    }

    // Remove a player from a game (e.g., on disconnect)
    // public void removePlayer(int playerId){
    //     Game game = findGameByPlayerId(playerId);
    //     if(game != null){
    //         if(game.getPlayer1ID() == playerId){
    //             game.Player1Quit(); // Implemented in Game.java
    //         } else {
    //             game.Player2Quit();
    //         }
    //     }
    // }

   //Player requests to quit and ends game
    public void playerQuit(int playerId){
        Game game = findGameByPlayerId(playerId);
        if(game == null){
            System.out.println("Player " + playerId + " is not in any active game.");
            return;
        }
        int gameNumber = game.gameNumber();

        int player1Id = game.getPlayer1ID();
        int player2Id = game.getPlayer2ID();
        int player1Score = game.getPlayer1Score();
        int player2Score = game.getPlayer2Score();
        int opponentId = -1;

        if(player1Id == playerId){
            game.Player1Quit();
            opponentId = player2Id;
            System.out.println("Player " + playerId + "(Player 1) quit game " + gameNumber);
        } else if (player2Id == playerId) {
            game.Player2Quit();
            opponentId = player1Id;
            System.out.println("Player " + playerId + "(Player 2) quit game " + gameNumber);
        } else {
            System.err.println("Error: Player " + playerId + " tried to quit game " + gameNumber + " but was not found as P1 or P2.");
            return;
        }

        Player winner = game.getWinner();
        int winningPlayerId = (winner != null) ? winner.getPlayerId() : -2;

        Map<Integer, Integer> playerScores = new HashMap<>();
        playerScores.put(player1Id, player1Score);
        playerScores.put(player2Id, player2Score);

        gt.endGame(playerScores, winningPlayerId);
        activeGames.remove(gameNumber);
        System.out.println("Player " + playerId + " quit. Game " + gameNumber + " ended and removed.");

    }
}
