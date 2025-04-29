package uta.cse3310.PageManager;

import uta.cse3310.GameManager.GamePageController;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.PageManager.UserEvent;
import uta.cse3310.PageManager.UserEventReply;
import uta.cse3310.PageManager.game_status;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.App;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GamePlay.GamePlay;
import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collections;

// Needs from GameManager:
// getAllowedMoves, getGameState, getGameStatus, getGameOver, getWinner, getLoser, getallPlayerIDs 

public class GameDisplayConnector {

    private GamePageController gamePageController;
    private GameTermination gameTermination;
    private GameManager gameManager;
    private GamePlay gp;
    private App appServer;


    public GameDisplayConnector(GamePageController gamePageController, GameTermination gameTermination, GameManager gameManager, App appServer) {
        this.gamePageController = gamePageController;
        this.gameTermination = gameTermination;
        this.gameManager = gameManager;
        this.appServer = appServer;
        this.gp = new GamePlay();
    }

    public String getPlayerName(int playerId) {
        if (playerId == 0) return "Bot I";
        if (playerId == 1) return "Bot II";
        return "Player " + playerId;
    }

    // Handle a move from the front-end
    public UserEventReply handleMoveRequest(UserEvent event) {
        System.out.println("[DEBUG] Received move from player " + event.id);

        // Get client ID from event
        UserEventReply humanMoveReply  = new UserEventReply();
        humanMoveReply.status = new game_status();
        humanMoveReply.recipients = new ArrayList<>();

        Game game = gamePageController.returnGame(event.id);
        int gameNumber = game.gameNumber();
        int[] allPlayerIds = gamePageController.getAllPlayerIDs(event.id);

        if (event.from != null && event.to != null) {
            Move humanPlayerMove = new Move(event.from[0], event.from[1], event.to[0], event.to[1]);
            gamePageController.processMove(event.id, humanPlayerMove);

            humanMoveReply.status.type = "move_made_by_other_player_or_bot";
            humanMoveReply.status.game_id = gameNumber;
            humanMoveReply.status.player = getPlayerName(event.id);
            humanMoveReply.status.from = List.of(event.from[0], event.from[1]);
            humanMoveReply.status.to = List.of(event.to[0], event.to[1]);


            Player playerAfterHumanMove = game.getCurrentTurn();
            humanMoveReply.status.current_move = getPlayerName(playerAfterHumanMove.getPlayerId());
            humanMoveReply.status.id = playerAfterHumanMove.getPlayerId();


            if (allPlayerIds != null) {
                for (int id : allPlayerIds) {
                    humanMoveReply.recipients.add(id);
                }
            }

            Player winner = game.getWinner();
            boolean draw = gamePageController.getDraw(event.id);
            if (winner != null || draw) {
                humanMoveReply.status.type = "game_over";
                humanMoveReply.status.gameOver = true;
                humanMoveReply.status.winner = (winner != null) ? winner.getPlayerId() : null;
                humanMoveReply.status.draw = draw;
                humanMoveReply.status.msg = draw ? "Game ended in a draw!" : "Player " + getPlayerName(winner.getPlayerId()) + " wins!";
                System.out.println("[DEBUG DisplayConnector] Game over after human move. " + humanMoveReply.status.msg);
                return humanMoveReply;
            }




            Player currentPlayer = playerAfterHumanMove;
            int currentPlayerId = currentPlayer.getPlayerId();
            boolean isBotTurn = (currentPlayerId == 0 || currentPlayerId == 1);

            if (isBotTurn && game.gameActive()) {
                System.out.println("[DEBUG DisplayConnector] Bot's turn (Player " + currentPlayerId + "). Requesting move...");

                Moves botMoves = gameManager.requestBotMoves(game, currentPlayerId);

                if (botMoves != null && botMoves.size() > 0) {
                    Move botMove = botMoves.getFirst();
                    System.out.println("[DEBUG DisplayConnector] Bot " + currentPlayerId + " chose move: " + botMove.getStart().getRow()+","+botMove.getStart().getCol() + " -> " + botMove.getDest().getRow()+","+botMove.getDest().getCol());

                    gp.returnBoard(game, botMoves);
                    game.switchTurn();
                    System.out.println("[DEBUG DisplayConnector] Bot move processed for game " + game.gameNumber());

                    UserEventReply botMoveReply = new UserEventReply();
                    botMoveReply.status = new game_status();
                    botMoveReply.recipients = new ArrayList<>();

                    botMoveReply.status.type = "move_made_by_other_player_or_bot";
                    botMoveReply.status.game_id = game.gameNumber();
                    botMoveReply.status.player = getPlayerName(currentPlayerId);
                    botMoveReply.status.from = List.of(botMove.getStart().getRow(), botMove.getStart().getCol());
                    botMoveReply.status.to = List.of(botMove.getDest().getRow(), botMove.getDest().getCol());

                    Player nextPlayerAfterBot = game.getCurrentTurn();
                    botMoveReply.status.current_move = getPlayerName(nextPlayerAfterBot.getPlayerId());
                    botMoveReply.status.id = nextPlayerAfterBot.getPlayerId();

                    winner = game.getWinner();
                    draw = gamePageController.getDraw(event.id);

                    if (winner != null || draw) {
                        botMoveReply.status.type = "game_over";
                        botMoveReply.status.gameOver = true;
                        botMoveReply.status.winner = (winner != null) ? winner.getPlayerId() : null;
                        botMoveReply.status.draw = draw;
                        botMoveReply.status.msg = draw ? "Game ended in a draw!" : "Player " + getPlayerName(winner.getPlayerId()) + " wins!";
                        System.out.println("[DEBUG DisplayConnector] Game over after bot move. " + botMoveReply.status.msg);
                    }


                    if (allPlayerIds != null) {
                        for (int id : allPlayerIds) {
                            botMoveReply.recipients.add(id);
                        }
                    }

                    try {
                        System.out.println("[DEBUG DisplayConnector] Adding 1-second delay before sending bot move...");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.err.println("[ERROR DisplayConnector] Thread sleep interrupted: " + e.getMessage());
                        Thread.currentThread().interrupt();
                    }

                    this.appServer.queueMessage(botMoveReply);

                } else {
                    System.out.println("[WARN DisplayConnector] Bot " + currentPlayerId + " did not return any moves.");
                    if (gamePageController.getDraw(event.id)) {
                        UserEventReply drawReply = new UserEventReply();
                        drawReply.status = new game_status();
                        drawReply.recipients = new ArrayList<>();
                        drawReply.status.type = "game_over";
                        drawReply.status.gameOver = true;
                        drawReply.status.draw = true;
                        drawReply.status.msg = "Game ended in a draw (no moves available)!";
                        if (allPlayerIds != null) {
                            for (int id : allPlayerIds) drawReply.recipients.add(id);
                        }
                        if (this.appServer != null) {
                            this.appServer.queueMessage(drawReply);
                            System.out.println("[DEBUG DisplayConnector] Queued message for draw after bot had no moves.");
                        }
                        humanMoveReply = drawReply;
                    }
                }
            } else {
                System.out.println("[DEBUG DisplayConnector] It's human player " + currentPlayerId + "'s turn. Waiting for input.");
            }
        } else {
            humanMoveReply.status.type = "error";
            humanMoveReply.status.msg = "Invalid move data.";
            humanMoveReply.recipients.add(event.id);
            System.out.println("[ERROR] Invalid move data received from player " + event.id);
        }


        return humanMoveReply;
    }

    // Handle resignation
    public UserEventReply handleResign(UserEvent event) {
        System.out.println("[DEBUG] Player " + event.id + " resigned.");

        // Get client ID from event
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        // Trigger game termination logic
        //gameTermination.terminateGame(event.gameId, event.id);

        reply.status.type = "resign";
        // hl reply.status.player = event.playerName + " (ID: " + event.id + ")";
        reply.status.player = getPlayerName(event.id);
        reply.status.gameOver = true;
        // Get all player IDs from GameManager
        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            for (int id : playerIds) {
                reply.recipients.add(id);
            }
        }

        return reply;
    }

    // Handle draw offer 
    public UserEventReply handleDrawOffer(UserEvent event) {
        System.out.println("[DEBUG] Player " + event.id + " offered a draw.");

        // Get client ID from event
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        // Send draw offer to other player
        reply.status.type = "draw_offer";
        // hl reply.status.player = event.playerName + " (ID: " + event.id + ")";
        reply.status.player = getPlayerName(event.id);

        // Get all player IDs from GameManager
        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            for (int id : playerIds) {
                if (id != event.id) {
                    reply.recipients.add(id);
                }
            }
        }

        return reply;
    }


    public UserEventReply handleDrawAccept(UserEvent event) {
        System.out.println("[DEBUG DisplayConnector] Handling draw acceptance from player " + event.id);
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();


        reply.status.type = "draw_accept";
        reply.status.player = getPlayerName(event.id);
        reply.status.gameOver = true;
        reply.status.draw = true;

        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            for (int id : playerIds) {
                reply.recipients.add(id);
            }
        }
        return reply;
    }

    // Get allowed moves 
    public UserEventReply handleGetAllowedMoves(UserEvent event) {
        System.out.println("[DEBUG] Getting allowed moves for square " + Arrays.toString(event.square) + " from" + getPlayerName(event.id));

        // Get client ID from event
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        reply.status.type = "valid_moves";

        // Use GamePageController to get legal moves from GamePlay
        Map<Square, Moves> moveMap = gamePageController.getAllowedMoves(event.id, event.square);

        List<List<Integer>> legalMoves = new ArrayList<>();
        if (moveMap != null) {
            for (Moves possible_moves_obj : moveMap.values()) {
                if (possible_moves_obj != null) {
                    LinkedList<Move> piece_moves = possible_moves_obj.getMoves();
                    if (piece_moves != null) {
                        for (Move move : piece_moves) {
                            if (move != null && move.getDest() != null) {
                                Square destSquare = move.getDest();
                                List<Integer> moves_to_destination = new ArrayList<>();
                                moves_to_destination.add(destSquare.getRow());
                                moves_to_destination.add(destSquare.getCol());
                                legalMoves.add(moves_to_destination);
                            }
                        }
                    }
                }
            }
        }

        if (moveMap != null && !moveMap.isEmpty()) {
            System.out.println("[DEBUG] moveMap details:");
            for (Map.Entry<Square, Moves> entry : moveMap.entrySet()) {
                Square s = entry.getKey();
                Moves moves = entry.getValue();
                System.out.println(" - From Square: (" + s.getRow() + "," + s.getCol() + ")");
                System.out.println("   Moves Info: " + moves.toString());  // assuming Moves has a good toString()
            }
        } else {
            System.out.println("[DEBUG] moveMap is empty");
        }

        System.out.println("[DEBUG] legalMoves size: " + legalMoves.size());

        reply.status.legal_moves = legalMoves;

        // Get all player IDs from GameManager
        // int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        // if (playerIds != null) {
        //     for (int id : playerIds) {
        //         reply.recipients.add(id);
        //     }
        // }

        reply.recipients.add(event.id);

        return reply;
    }

    // Send game board to creator for a Bot vs Bot match
    public UserEventReply sendBotVsBotBoard(int creatorId, int gameId) {
        System.out.println("[DEBUG] Sending Bot vs Bot board to creator ID: " + creatorId);

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        reply.status.type = "show_game_display";
        reply.status.game_id = gameId;
        reply.status.msg = "You are now watching a Bot vs Bot match.";


        reply.status.player = getPlayerName(creatorId); // spectator name
        reply.status.playerId = creatorId; // spectator id
        reply.status.player_color = "S"; // spectator


        Game game = gamePageController.returnGame(gameId);
        if (game != null) {
            reply.status.starting_player = getPlayerName(game.getCurrentTurn().getPlayerId());
        } else {
            reply.status.starting_player = "Bot";
            System.out.println("[WARN] Could not get game details for BotVsBot game ID: " + gameId);
        }


        reply.status.clientId = creatorId;
        reply.recipients.add(creatorId);

        return reply;
    }

    public UserEventReply sendShowGameDisplay(int clientId) {
        System.out.println("[DEBUG-DisplayConnector] Entered sendShowGameDisplay for clientId: " + clientId);

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        Game game = gamePageController.returnGame(clientId);

        if (game == null) {
            System.out.println("[DEBUG-DisplayConnector] No active game found for client ID " + clientId + ". Returning null.");
            return null;
        }

        System.out.println("[DEBUG-DisplayConnector] Found active game " + game.gameNumber() + " for clientId " + clientId);

        reply.status.type = "show_game_display";
        reply.status.game_id = game.gameNumber();
        reply.status.player = getPlayerName(clientId);
        reply.status.playerId = clientId;

        boolean isPlayer1 = (game.getPlayer1ID() == clientId);
        boolean isPlayer2 = (game.getPlayer2ID() == clientId);
        boolean colorIsWhite = false; // Default assumption
        String playerColor = "S";

        if (isPlayer1) {
            colorIsWhite = game.getPlayer1Color();
            playerColor = colorIsWhite ? "W" : "B";
        } else if (isPlayer2) {
            colorIsWhite = game.getPlayer2Color();
            playerColor = colorIsWhite ? "W" : "B";
        } else {
            System.out.println("[DEBUG DisplayConnector] Client ID " + clientId + " not found as P1 or P2 in game " + game.gameNumber() + ". Assigning spectator color.");
        }

        reply.status.player_color = playerColor;
        Player startingPlayer = game.getCurrentTurn();

        if (startingPlayer != null) {
            reply.status.starting_player = getPlayerName(startingPlayer.getPlayerId());
            System.out.println("[DEBUG DisplayConnector] Starting player: " + reply.status.starting_player);
        } else {
            System.out.println("[WARN DisplayConnector] Could not determine starting player for game " + game.gameNumber());
        }


        // Add the client ID as a recipient for this reply
        reply.recipients.add(clientId);

        return reply;
    }

    // Test-only dummy mehthod for the GameDisplay group
    public UserEventReply sendShowGameDisplayTest(UserEvent event) {
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        // Hardcoded values for testing
        reply.status.type = "show_game_display";
        reply.status.game_id = 123; // dummy game ID
        reply.status.player = "Luigi (ID: 1)";
        reply.status.player_color = "B"; // or "W"
        reply.status.starting_player = "Mario (ID: 2)"; // i changed this from int to a string

        // Send to both players (hardcoded values for test)
        reply.recipients.add(1); // Luigi
        reply.recipients.add(2); // Mario

        return reply;
    }


    // public String getPlayerName(int playerId) {
    //     if (this.gameManager == null) {
    //         System.out.println("[ERROR] GameManager instance is null in getPlayerName. Cannot find player name.");
    //         return "Player " + playerId;
    //     }
    //     String formattedName = "Player " + playerId;
    //     return formattedName;
    // }

}
