package uta.cse3310.GameManager;

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
        gp = new GamePlay(); // Handles game rules and moves
        gt = new GameTermination(); // Handles game-ending logic
        grc = new GamePairController();
    }
    public void initializeGames(){
        games = new ArrayList<>(MAXIMUM_GAMES);
        for (int i = 0; i < MAXIMUM_GAMES; ++i){
            games.add(null);
        }
    }
    // Create a new game with two players
    public boolean createGame(Match match){
        int availableSlot = getAvailableGameSlot();
        if(availableSlot == -1){
            System.out.println("No available game slots.");
            return false;
        }
        Game newGame = grc.newMatch(match);
        games.set(availableSlot, newGame);
        
        return true;
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
            Moves moves = new Moves();
            moves.addNext(move);
            gp.returnBoard(game, moves); // from GamePlay.java
            game.switchTurn();
        } else {
            System.out.println("Invalid move: game not found or inactive.");
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

    private Moves requestBotMoves(Game game, int botId){
        if(botId == 0){
            BotI bot = new BotI();
            bot.setColor(game.getCurrentTurn().getColor());
            return bot.requestMove(game.getBoard());
        } else if(botId == 1){
            BotII bot = new BotII();
            bot.setColor(game.getCurrentTurn().getColor());
            return bot.requestMove(game.getBoard());
        }
        return null;
    }

    public void progressGame(int gameNumber){
        Game game = games.get(gameNumber);
        if(game == null || !game.gameActive()){
            return;
        }
        
        // Get current player
        Player currentPlayer = game.getCurrentTurn();
        int playerId = currentPlayer.getPlayerId();
        
        // Check if current player is a bot
        boolean isBot = false;
        if((playerId == game.getPlayer1ID() && game.isPlayer1Bot()) || 
            (playerId == game.getPlayer2ID() && game.isPlayer2Bot())){
            isBot = true;
        }
        
        if(isBot){
            // For bot players, request moves from the bot
            Moves botMoves = requestBotMoves(game, playerId);
            
            if(botMoves != null && botMoves.size() > 0){
                // Send moves to GamePlay and get updated board
                Board updatedBoard = gp.returnBoard(game, botMoves);
                
                if(updatedBoard != null){
                    // Valid move - update the board
                    game.updateBoard(updatedBoard);
                    
                    // Let GameTermination check if the game is over
                    boolean gameOver = false;//gt.isGameOver(game);
                    if(!gameOver){
                        // If not over, switch turns
                        game.switchTurn();
                    }
                }
            }
        }
        // For human players, we wait for input through processMove
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
