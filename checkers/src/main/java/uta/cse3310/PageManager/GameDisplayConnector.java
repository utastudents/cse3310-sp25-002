package uta.cse3310.PageManager;

import uta.cse3310.GameManager.GamePageController;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.PageManager.UserEventReply;
import uta.cse3310.PageManager.game_status;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.App;
import uta.cse3310.GamePlay.GamePlay;
import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collections;
import uta.cse3310.GamePlay.rules;


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
        System.out.println("[DEBUG DisplayConnector] Handling move request from player " + event.id);

        UserEventReply finalReply = new UserEventReply();
        finalReply.status = new game_status();
        finalReply.recipients = new ArrayList<>(Collections.singletonList(event.id));

        Game game = gamePageController.returnGame(event.id);

        if (game == null) {
            System.err.println("[ERROR DisplayConnector] Game not found for player " + event.id);
            finalReply.status.type = "error";
            finalReply.status.msg = "Error: Game not found.";
            return finalReply;
        }

        if (!game.gameActive()) {
            System.out.println("[WARN DisplayConnector] Move received for inactive game " + game.gameNumber() + " from player " + event.id);
            finalReply.status.type = "error";
            finalReply.status.msg = "Error: Game is not active.";
            finalReply.status.gameOver = true;
            finalReply.status.winner = game.getWinner() != null ? game.getWinner().getPlayerId() : null;
            finalReply.status.draw = game.isDraw();
            return finalReply;
    }

        Player currentTurnPlayer = game.getCurrentTurn();
        if (currentTurnPlayer == null || currentTurnPlayer.getPlayerId() != event.id) {
            finalReply.status.type = "error";
            finalReply.status.msg = "Error: Not your turn.";
            return finalReply;
        }


        if (event.from == null || event.to == null || event.from.length != 2 || event.to.length != 2) {
            System.err.println("[ERROR DisplayConnector] Invalid move data format from player " + event.id);
            finalReply.status.type = "error";
            finalReply.status.msg = "Error: Invalid move data format.";
            return finalReply;
        }

        Move humanPlayerMove = new Move(
            new Square(event.from[0], event.from[1]),
            new Square(event.to[0], event.to[1])
        );


        boolean moveExecuted = gp.processAndExecuteMove(game, humanPlayerMove);

        if (!moveExecuted) {
            System.out.println("[WARN DisplayConnector] Illegal move attempted by player " + event.id + ": (" + event.from[0] + "," + event.from[1] + ")->(" + event.to[0] + "," + event.to[1] + ")");
            finalReply.status.type = "error";
            finalReply.status.msg = "Illegal move attempt.";
            return finalReply;
        }

        System.out.println("[DEBUG DisplayConnector] Human move executed successfully by player " + event.id);

        Player winner = game.getWinner();
        boolean draw = game.checkDrawCondition();


        UserEventReply notify_players = new UserEventReply();
        notify_players.status = new game_status();
        notify_players.recipients = new ArrayList<>();

        notify_players.status.game_id = game.gameNumber();
        notify_players.status.player = getPlayerName(event.id);
        notify_players.status.from = List.of(event.from[0], event.from[1]);
        notify_players.status.to = List.of(event.to[0], event.to[1]);

        int player1Id = game.getPlayer1ID();
        int player2Id = game.getPlayer2ID();
        int opponentId = -1;
        if (player1Id == event.id) {
            opponentId = player2Id;
        } else if (player2Id == event.id) {
            opponentId = player1Id;
        }

        if (winner != null || draw) {
            notify_players.status.type = "game_over";
            notify_players.status.gameOver = true;
            notify_players.status.winner = (winner != null) ? winner.getPlayerId() : null;
            notify_players.status.draw = draw;
            notify_players.status.msg = draw ? "Game ended in a draw!" : "Player " + getPlayerName(winner.getPlayerId()) + " wins!";
            System.out.println("[DEBUG DisplayConnector] Game over after human move. " + notify_players.status.msg);

            game.endGame();

            if (player1Id > 1) notify_players.recipients.add(player1Id);
            if (player2Id > 1) notify_players.recipients.add(player2Id);

            if (!notify_players.recipients.isEmpty()) {
                appServer.queueMessage(notify_players);
            }
            finalReply.status.type = "hide_game_display";
            finalReply.status.msg = "Move processed. Game Over.";
            finalReply.status.gameOver = true;
            finalReply.status.winner = notify_players.status.winner;
            finalReply.status.draw = notify_players.status.draw;

            return finalReply;


        } else {
            game.switchTurn();
            Player nextPlayer = game.getCurrentTurn();

            notify_players.status.type = "move_made_by_other_player_or_bot";
            notify_players.status.current_move = getPlayerName(nextPlayer.getPlayerId());
            notify_players.status.id = nextPlayer.getPlayerId();

            if (opponentId > 1) {
                notify_players.recipients.add(opponentId);
            }

            if (!notify_players.recipients.isEmpty()) {
                appServer.queueMessage(notify_players);
                System.out.println("[DEBUG DisplayConnector] Queued move notification to opponent: " + opponentId);
            }


            boolean isNextTurnBot = (nextPlayer.getPlayerId() == 0 || nextPlayer.getPlayerId() == 1);
            if (isNextTurnBot && game.gameActive()) {
                System.out.println("[DEBUG DisplayConnector] Bot's turn (Player " + nextPlayer.getPlayerId() + ") after human move. Requesting move...");
                try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                Moves botMovesList = gameManager.requestBotMoves(game, nextPlayer.getPlayerId());
                boolean botMoveExecuted = false;
                Move successfulBotMove = null;

                    if (botMovesList != null && botMovesList.size() > 0) {
                        for (Move botAttempt : botMovesList.getMoves()) {
                            if (rules.canMovePiece(game, botAttempt)) {
                                if (gp.processAndExecuteMove(game, botAttempt)) {
                                    botMoveExecuted = true;
                                    successfulBotMove = botAttempt;
                                    break;
                                } else {
                                    System.err.println("[ERROR DisplayConnector] Bot move failed execution even though rules said OK.");
                                }
                            } else {
                                System.out.println("[WARN DisplayConnector] Bot attempt " + botAttempt + " illegal, trying next.");
                            }
                        }
                    }

                    if (botMoveExecuted) {
                        System.out.println("[DEBUG DisplayConnector] Bot move executed successfully for game " + game.gameNumber());
                        Player botWinner = game.getWinner();
                        boolean botDraw = game.checkDrawCondition();

                        UserEventReply botResponseNotification = new UserEventReply();
                        botResponseNotification.status = new game_status();
                        botResponseNotification.recipients = new ArrayList<>();
                        if (player1Id > 1) botResponseNotification.recipients.add(player1Id);
                        if (player2Id > 1) botResponseNotification.recipients.add(player2Id);

                        botResponseNotification.status.game_id = game.gameNumber();
                        botResponseNotification.status.player = getPlayerName(nextPlayer.getPlayerId());
                        botResponseNotification.status.from = List.of(successfulBotMove.getStart().getRow(), successfulBotMove.getStart().getCol());
                        botResponseNotification.status.to = List.of(successfulBotMove.getDest().getRow(), successfulBotMove.getDest().getCol());

                        if (botWinner != null || botDraw) {
                            botResponseNotification.status.type = "game_over";
                            game.endGame();
                        } else {
                            game.switchTurn();
                            Player finalNextPlayer = game.getCurrentTurn();
                            botResponseNotification.status.type = "move_made_by_other_player_or_bot";
                        }
                        if (!botResponseNotification.recipients.isEmpty()) {
                            appServer.queueMessage(botResponseNotification);
                        }

                    } else {
                        System.err.println("[ERROR DisplayConnector] Bot " + nextPlayer.getPlayerId() + " failed to make a valid move.");
                    }

                } else if (!isNextTurnBot) {
                    System.out.println("[DEBUG DisplayConnector] It's human player " + nextPlayer.getPlayerId() + "'s turn after previous human move. Waiting for input.");
                }

            finalReply.status.type = "move_ack";
            finalReply.status.msg = "Move processed successfully.";
            finalReply.status.current_move = getPlayerName(game.getCurrentTurn().getPlayerId());
            finalReply.status.id = game.getCurrentTurn().getPlayerId();
            return finalReply;
        }
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

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>(Collections.singletonList(event.id));

        reply.status.type = "valid_moves";
        Game game = gamePageController.returnGame(event.id);


            boolean playerColor;
            if (game.getPlayer1ID() == event.id) playerColor = game.getPlayer1Color();
            else if (game.getPlayer2ID() == event.id) playerColor = game.getPlayer2Color();
            else {
                reply.status.type = "error";
                reply.status.msg = "Only players can request moves.";
                reply.status.legal_moves = new ArrayList<>();
                return reply;
        }

        Moves movesForPiece = rules.getMovesForSquare(game.getBoard(), playerColor, event.square);

        List<List<Integer>> legalMovesList = new ArrayList<>();
        if (movesForPiece != null) {
            LinkedList<Move> piece_moves = movesForPiece.getMoves();
            if (piece_moves != null) {
                for (Move move : piece_moves) {
                    if (move != null && move.getDest() != null) {
                        Square destSquare = move.getDest();
                        List<Integer> destCoords = new ArrayList<>();
                        destCoords.add(destSquare.getRow());
                        destCoords.add(destSquare.getCol());
                        legalMovesList.add(destCoords);
                    }
                }
            }
        }



        System.out.println("[DEBUG] legalMoves size: " + legalMovesList.size());

        reply.status.legal_moves = legalMovesList;
        // Get all player IDs from GameManager
        // int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        // if (playerIds != null) {
        //     for (int id : playerIds) {
        //         reply.recipients.add(id);
        //     }
        // }

        // imp remember this later HL reply.recipients.add(event.id);
        return reply;
    }

    // Send game board to creator for a Bot vs Bot match
    public UserEventReply sendBotVsBotBoard(int spectatorId, int gameId) {
        System.out.println("[DEBUG] Sending Bot vs Bot board to creator ID: " + spectatorId);

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>(Collections.singletonList(spectatorId));

        Game game = gameManager.findGameByPlayerId(gameId);

        reply.status.type = "show_game_display";
        reply.status.game_id = gameId;
        reply.status.msg = "Spectating Bot vs Bot match.";
        reply.status.player = getPlayerName(spectatorId);
        reply.status.playerId = spectatorId;
        reply.status.player_color = "S";


        if (game != null) {
            reply.status.starting_player = getPlayerName(game.getCurrentTurn().getPlayerId());
        } else {
            reply.status.starting_player = "Bot I";
            System.out.println("[WARN] Could not get game details for BotVsBot game ID: " + gameId);
        }


        reply.status.clientId = spectatorId;

        return reply;
    }

    public UserEventReply sendShowGameDisplay(int clientId) {
        System.out.println("[DEBUG-DisplayConnector] Entered sendShowGameDisplay for clientId: " + clientId);

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>(Collections.singletonList(clientId));


        Game game = gamePageController.returnGame(clientId);

        if (game == null) {
            System.out.println("[DEBUG-DisplayConnector] No active game found for client ID " + clientId + ". Returning null.");
            reply.status.type = "error";
            reply.status.msg = "Could not find your game.";
            return null;
        }

        System.out.println("[DEBUG-DisplayConnector] Found active game " + game.gameNumber() + " for clientId " + clientId);

        reply.status.type = "show_game_display";
        reply.status.game_id = game.gameNumber();
        reply.status.player = getPlayerName(clientId);
        reply.status.playerId = clientId;

        boolean isPlayer1 = (game.getPlayer1ID() == clientId);
        boolean isPlayer2 = (game.getPlayer2ID() == clientId);
        String playerColor = "S";

        if (isPlayer1) {
            playerColor = game.getPlayer1Color() ? "W" : "B";
        } else if (isPlayer2) {
            playerColor = game.getPlayer2Color() ? "W" : "B";
        } else {
            System.out.println("[WARN DisplayConnector] Client ID " + clientId + " not found as P1 or P2 in game " + game.gameNumber() + ". Assigning spectator color.");
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
