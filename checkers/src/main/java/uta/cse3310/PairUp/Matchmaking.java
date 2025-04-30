package uta.cse3310.PairUp;

import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.Game;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.*;
import uta.cse3310.PageManager.PageManager;

/*
    The Matchmaking class will deal with all things
    matchmaking. It holds all the players that are
    looking for a match. It deals with pairing them
    up, and removes them from matchmaking once they
    find a match, or if they quit matchmaking.
*/
public class Matchmaking {
    public LinkedHashMap<Integer, PlayerInMatchmaking> players;
    private int gameId;
    private GameManager gameManagerCommunication;
    private MatchmakingScheduler scheduler;
    private PageManager pageManager;

    public Matchmaking(PageManager pageManager, GameManager gameManager) {
        players = new LinkedHashMap<>();
        gameId = 0;
        this.gameManagerCommunication = gameManager;
        this.pageManager = pageManager;
        this.scheduler = new MatchmakingScheduler(this);
        this.scheduler.start();
    }

    // Pairs two players
    public Game pair(PlayerInMatchmaking p1, PlayerInMatchmaking p2) {
        System.out.println("[DEBUG-Matchmaking] Attempting to pair players: " + p1.getPlayerID() + " and " + p2.getPlayerID());
        Random coinflip = new Random();
        boolean p1Color = coinflip.nextBoolean();
        boolean p2Color = !p1Color;
        Match match = new Match(p1.getPlayerID(), p2.getPlayerID(), p1.getPlayerName(), p2.getPlayerName(), false, gameId++, p1Color, p2Color);
        System.out.println("[DEBUG-Matchmaking] Created Match object with GameID: " + match.getGameId());
        Game newGame = gameManagerCommunication.createGame(match);
        System.out.println("[DEBUG-Matchmaking] Created Game instance with GameID: " + (newGame != null ? newGame.gameNumber() : "NULL"));


        if (pageManager != null && newGame != null) {
            System.out.println("[DEBUG-Matchmaking] Calling triggerGameDisplay for GameID: " + newGame.gameNumber() + ", P1=" + p1.getPlayerID() + ", P2=" + p2.getPlayerID());
            pageManager.triggerGameDisplay(newGame.gameNumber(), p1.getPlayerID(), p2.getPlayerID());
        } else {
            System.out.println("[DEBUG-Matchmaking] pageManager is null or newGame is null. Cannot trigger game display.");
        }

        return newGame;
    }

    // Pairs a player and bot
    public Game pair(PlayerInMatchmaking p1, int botID) {
        Random coinflip = new Random();
        boolean p1Color = coinflip.nextBoolean();
        boolean botColor = !p1Color;
        Match match = new Match(p1.getPlayerID(), botID, p1.getPlayerName(), "Bot", true, gameId++, p1Color, botColor);
        System.out.println("[DEBUG] Created a Bot vs Player game with ClientID: " + p1.getPlayerID() + " and BotID: " + botID);
        Game newGame = gameManagerCommunication.createGame(match);

        System.out.println("[DEBUG-Matchmaking PvB] Game created: " + (newGame != null ? newGame.gameNumber() : "NULL"));
        if (pageManager != null && newGame != null) {
            System.out.println("[DEBUG-Matchmaking PvB] Calling triggerGameDisplay for GameID: " + newGame.gameNumber() + ", P1=" + p1.getPlayerID() + ", BotID=" + botID);
            pageManager.triggerGameDisplay(newGame.gameNumber(), p1.getPlayerID(), botID);
        } else {
            System.out.println("[ERROR-Matchmaking PvB] Cannot trigger game display. pageManager is null? " + (pageManager == null) + ", newGame is null? " + (newGame == null));
        }

        return newGame;
    }

    // Pairs a bot and bot
    public Game pair(int bot1ID, int bot2ID, int creatorID) {
        Match match = new Match(bot1ID, bot2ID, gameId++, creatorID);
        Game newGame = gameManagerCommunication.createGame(match);

        if (pageManager != null && newGame != null) {
            pageManager.triggerGameDisplay(newGame.gameNumber(), bot1ID, bot2ID);
        }

        return newGame;
    }

    public void addPlayer(int PlayerID, PlayerInMatchmaking newPlayer) {
        if (newPlayer.isPlayAgainstBot()) {
            int botID = (int) (Math.random() * 2);
            pair(newPlayer, botID);
        }
        else {
            /*System.out.println("Before:");
            for (Map.Entry<Integer, PlayerInMatchmaking> entry : players.entrySet()) {
                System.out.println(entry.getKey() + " => " + entry.getValue());
            }*/
            players.put(PlayerID, newPlayer);
            /*System.out.println("After:");
            for (Map.Entry<Integer, PlayerInMatchmaking> entry : players.entrySet()) {
                System.out.println(entry.getKey() + " => " + entry.getValue());
            }*/
            matching();
        }
    }

    public void removePlayer(int playerId) {
        players.remove(playerId);
        matching();
    }

    public Boolean getPlayer(int playerId)
    {
        if (players.get(playerId) != null) {
            return true;
        }
        else
        {
            return false;
        }
    }

    public synchronized void matching() {
        System.out.println("[DEBUG-Matchmaking] Starting matching process.");
        System.out.println("[DEBUG-Matchmaking] Players in queue: " + players.size());
        List<Map.Entry<Integer, PlayerInMatchmaking>> entries = new ArrayList<>(players.entrySet());

        for (int i = 0; i < entries.size(); i++) {
            PlayerInMatchmaking p1 = entries.get(i).getValue();
            System.out.println("[DEBUG-Matchmaking] Considering P1: " + p1.getPlayerID() + " (Wins: " + p1.getWins() + ", QueueTime: " + p1.getQueueTime() + ")");

            // Skip if player has already been matched
            if (!players.containsKey(entries.get(i).getKey())) {
                System.out.println("[DEBUG-Matchmaking] P1 " + p1.getPlayerID() + " already matched or removed, skipping.");
                continue;
            }

            boolean matched = false;
            // First try to match with players within +/- 1 win
            for (int j = i + 1; j < entries.size() && !matched; j++) {
                PlayerInMatchmaking p2 = entries.get(j).getValue();

                // Skip if player has already been matched
                if (!players.containsKey(entries.get(j).getKey())) {
                    System.out.println("[DEBUG-Matchmaking] P2 " + p2.getPlayerID() + " already matched or removed, skipping.");
                    continue;
                }

                System.out.println("[DEBUG-Matchmaking] Considering P2: " + p2.getPlayerID() + " (Wins: " + p2.getWins() + ")");
                int winDifference = Math.abs(p1.getWins() - p2.getWins());
                System.out.println("[DEBUG-Matchmaking] Win difference between " + p1.getPlayerID() + " and " + p2.getPlayerID() + ": " + winDifference);

                if (winDifference <= 1) {
                    System.out.println("[DEBUG-Matchmaking] Found potential match by wins: P1=" + p1.getPlayerID() + ", P2=" + p2.getPlayerID());
                    pair(p1, p2);
                    players.remove(p1.getPlayerID());
                    players.remove(p2.getPlayerID());
                    System.out.println("[DEBUG-Matchmaking] Paired and removed: P1=" + p1.getPlayerID() + ", P2=" + p2.getPlayerID());
                    matched = true;
                }
            }

            // If no match found and player has been waiting > 60 seconds, match with next available player
            if (!matched && p1.getQueueTime() > 60000) { // 60000 milliseconds = 60 seconds
                System.out.println("[DEBUG-Matchmaking] P1 " + p1.getPlayerID() + " waiting over 60s. Queue time: " + p1.getQueueTime());

                for (int j = i + 1; j < entries.size() && !matched; j++) {
                    PlayerInMatchmaking p2 = entries.get(j).getValue();

                    if (!players.containsKey(entries.get(j).getKey())) {
                        System.out.println("[DEBUG-Matchmaking] P2 " + p2.getPlayerID() + " already matched or removed in timeout check, skipping.");
                        continue;
                    }

                    System.out.println("[DEBUG-Matchmaking] Found potential match by timeout: P1=" + p1.getPlayerID() + ", P2=" + p2.getPlayerID());
                    pair(p1, p2);
                    players.remove(p1.getPlayerID());
                    players.remove(p2.getPlayerID());
                    System.out.println("[DEBUG-Matchmaking] Paired and removed by timeout: P1=" + p1.getPlayerID() + ", P2=" + p2.getPlayerID());
                    matched = true;
                }
            }
            if (!matched) {
                System.out.println("[DEBUG-Matchmaking] No match found for P1 " + p1.getPlayerID() + " in this run.");
            }
        }
        System.out.println("[DEBUG-Matchmaking] Matching process finished.");
    }



}