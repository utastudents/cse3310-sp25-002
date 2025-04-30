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

        boolean wasCapture = false;
        int middleRow = -1, middleCol = -1;
        int rowDiff = Math.abs(event.to[0] - event.from[0]);
        if (rowDiff == 2) {
            wasCapture = true;
            middleRow = (event.from[0] + event.to[0]) / 2;
            middleCol = (event.from[1] + event.to[1]) / 2;
            System.out.println("[DEBUG DisplayConnector] Capture assumed by distance. Captured square: [" + middleRow + ", " + middleCol + "]");
        }

        Player winner = game.getWinner();
        boolean draw = game.checkDrawCondition();

        int player1Id = game.getPlayer1ID();
        int player2Id = game.getPlayer2ID();
        int opponentId = (player1Id == event.id) ? player2Id : player1Id;

        if (winner != null || draw) {
            UserEventReply gameOverNotification = new UserEventReply();
            gameOverNotification.status = new game_status();
            gameOverNotification.recipients = new ArrayList<>();
            if (player1Id > 1) gameOverNotification.recipients.add(player1Id);
            if (player2Id > 1) gameOverNotification.recipients.add(player2Id);

            gameOverNotification.status.type = "game_over";
            gameOverNotification.status.gameOver = true;
            gameOverNotification.status.game_id = game.gameNumber();
            if (wasCapture) {
                gameOverNotification.status.capturedSquare = List.of(middleRow, middleCol);
            }

            if (draw) {
                gameOverNotification.status.winner = null;
                gameOverNotification.status.draw = true;
                // *** SPECIFIC MESSAGE FOR DRAW ***
                gameOverNotification.status.msg = "Game ended: It's a draw!";
            } else {
                gameOverNotification.status.winner = winner.getPlayerId();
                gameOverNotification.status.draw = false;
                gameOverNotification.status.msg = "Game ended: Player " + getPlayerName(winner.getPlayerId()) + " wins!";
            }
            System.out.println("[DEBUG DisplayConnector] Game over after human move. " + gameOverNotification.status.msg);

            gameManager.terminateGame(game.gameNumber());

            if (!gameOverNotification.recipients.isEmpty()) {
                appServer.queueMessage(gameOverNotification);
            }

            finalReply.status.type = "move_ack";
            finalReply.status.msg = "Move processed. Game Over: " + gameOverNotification.status.msg;
            finalReply.status.gameOver = true;
            finalReply.status.winner = gameOverNotification.status.winner;
            finalReply.status.draw = gameOverNotification.status.draw;
            if (wasCapture) {
                finalReply.status.capturedSquare = List.of(middleRow, middleCol);
            }
            return finalReply;

        } else {
            game.switchTurn();
            Player nextPlayer = game.getCurrentTurn();

            UserEventReply notify_opponent = new UserEventReply();
            notify_opponent.status = new game_status();
            notify_opponent.recipients = new ArrayList<>();
            notify_opponent.status.game_id = game.gameNumber();
            notify_opponent.status.player = getPlayerName(event.id);
            notify_opponent.status.from = List.of(event.from[0], event.from[1]);
            notify_opponent.status.to = List.of(event.to[0], event.to[1]);
            if (wasCapture) {
                notify_opponent.status.capturedSquare = List.of(middleRow, middleCol);
            }
            notify_opponent.status.type = "move_made_by_other_player_or_bot";
            notify_opponent.status.current_move = getPlayerName(nextPlayer.getPlayerId());
            notify_opponent.status.id = nextPlayer.getPlayerId();

            if (opponentId > 1) {
                notify_opponent.recipients.add(opponentId);
            }
            if (!notify_opponent.recipients.isEmpty()) {
                appServer.queueMessage(notify_opponent);
                System.out.println("[DEBUG DisplayConnector] Queued move notification to opponent: " + opponentId);
            }

            boolean isNextTurnBot = (nextPlayer.getPlayerId() == 0 || nextPlayer.getPlayerId() == 1);
            if (isNextTurnBot && game.gameActive()) {
                System.out.println("[DEBUG DisplayConnector] Bot's turn (Player " + nextPlayer.getPlayerId() + ") after human move. Requesting move...");
                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                Moves botMovesList = gameManager.requestBotMoves(game, nextPlayer.getPlayerId());
                boolean botMoveExecuted = false;
                Move successfulBotMove = null;
                boolean botCapture = false;
                int botMiddleRow = -1, botMiddleCol = -1;

                if (botMovesList != null && botMovesList.size() > 0) {
                    for (Move botAttempt : botMovesList.getMoves()) {
                        if (rules.canMovePiece(game, botAttempt)) {
                            if (gp.processAndExecuteMove(game, botAttempt)) {
                                botMoveExecuted = true;
                                successfulBotMove = botAttempt;
                                int botRowDiff = Math.abs(successfulBotMove.getDest().getRow() - successfulBotMove.getStart().getRow());
                                if (botRowDiff == 2) {
                                    botCapture = true;
                                    botMiddleRow = (successfulBotMove.getStart().getRow() + successfulBotMove.getDest().getRow()) / 2;
                                    botMiddleCol = (successfulBotMove.getStart().getCol() + successfulBotMove.getDest().getCol()) / 2;
                                    System.out.println("[DEBUG DisplayConnector] Bot capture detected. Captured square: [" + botMiddleRow + ", " + botMiddleCol + "]");
                                }
                                break;
                            } else {
                                System.err.println("[ERROR DisplayConnector] Bot move failed execution even though rules said OK for move: (" + botAttempt.getStart().getRow()+","+botAttempt.getStart().getCol()+") -> ("+botAttempt.getDest().getRow()+","+botAttempt.getDest().getCol() + ")");
                            }
                        } else {
                            System.out.println("[WARN DisplayConnector] Bot attempt (" + botAttempt.getStart().getRow() + "," + botAttempt.getStart().getCol() + ") -> (" + botAttempt.getDest().getRow() + "," + botAttempt.getDest().getCol() + ") illegal, trying next.");
                        }
                    }
                }

                if (botMoveExecuted) {
                    System.out.println("[DEBUG DisplayConnector] Bot move executed successfully for game " + game.gameNumber());
                    Player botWinner = game.getWinner();
                    boolean botDraw = game.checkDrawCondition();

                    if (botWinner != null || botDraw) {
                        UserEventReply botGameOverNotification = new UserEventReply();
                        botGameOverNotification.status = new game_status();
                        botGameOverNotification.recipients = new ArrayList<>();
                        if (player1Id > 1) botGameOverNotification.recipients.add(player1Id);
                        if (player2Id > 1) botGameOverNotification.recipients.add(player2Id);

                        botGameOverNotification.status.game_id = game.gameNumber();
                        botGameOverNotification.status.type = "game_over";
                        botGameOverNotification.status.gameOver = true;
                        if (botCapture) {
                        botGameOverNotification.status.capturedSquare = List.of(botMiddleRow, botMiddleCol);
                        }

                        if (botDraw) {
                            botGameOverNotification.status.winner = null;
                            botGameOverNotification.status.draw = true;
                            botGameOverNotification.status.msg = "Game ended: It's a draw!";
                        } else {
                            botGameOverNotification.status.winner = botWinner.getPlayerId();
                            botGameOverNotification.status.draw = false;
                            botGameOverNotification.status.msg = "Game ended: Player " + getPlayerName(botWinner.getPlayerId()) + " wins!";
                        }

                        gameManager.terminateGame(game.gameNumber());

                        if (!botGameOverNotification.recipients.isEmpty()){
                            appServer.queueMessage(botGameOverNotification);
                            System.out.println("[DEBUG DisplayConnector] Queued game over notification after bot move.");
                        }

                    } else {
                        game.switchTurn();
                        Player humanPlayerNext = game.getCurrentTurn();

                        UserEventReply botResponseNotification = new UserEventReply();
                        botResponseNotification.status = new game_status();
                        botResponseNotification.recipients = new ArrayList<>();
                        if (player1Id > 1) botResponseNotification.recipients.add(player1Id);
                        if (player2Id > 1) botResponseNotification.recipients.add(player2Id);

                        botResponseNotification.status.game_id = game.gameNumber();
                        botResponseNotification.status.player = getPlayerName(nextPlayer.getPlayerId()); // Who made the move (the bot)
                        botResponseNotification.status.from = List.of(successfulBotMove.getStart().getRow(), successfulBotMove.getStart().getCol());
                        botResponseNotification.status.to = List.of(successfulBotMove.getDest().getRow(), successfulBotMove.getDest().getCol());
                        if (botCapture) {
                            botResponseNotification.status.capturedSquare = List.of(botMiddleRow, botMiddleCol);
                        }
                        botResponseNotification.status.type = "move_made_by_other_player_or_bot";
                        botResponseNotification.status.current_move = getPlayerName(humanPlayerNext.getPlayerId());
                        botResponseNotification.status.id = humanPlayerNext.getPlayerId();

                        appServer.queueMessage(botResponseNotification);
                        System.out.println("[DEBUG DisplayConnector] Queued bot move notification to players: " + botResponseNotification.recipients);
                    }

                } else {
                    System.err.println("[ERROR DisplayConnector] Bot " + nextPlayer.getPlayerId() + " failed to make a valid move in game " + game.gameNumber());
                    if (game.checkDrawCondition()) {
                        UserEventReply drawNotification = new UserEventReply();
                        drawNotification.status = new game_status();
                        drawNotification.recipients = new ArrayList<>();
                        if (player1Id > 1) drawNotification.recipients.add(player1Id);
                        if (player2Id > 1) drawNotification.recipients.add(player2Id);

                        drawNotification.status.type = "game_over";
                        drawNotification.status.gameOver = true;
                        drawNotification.status.draw = true;
                        drawNotification.status.winner = null;
                        drawNotification.status.msg = "Game ended: Draw by stalemate (Bot " + getPlayerName(nextPlayer.getPlayerId()) + " has no valid moves)!";
                        drawNotification.status.game_id = game.gameNumber();

                        gameManager.terminateGame(game.gameNumber());
                        appServer.queueMessage(drawNotification);
                    }
                }
            } else if (!isNextTurnBot) {
                System.out.println("[DEBUG DisplayConnector] It's human player " + nextPlayer.getPlayerId() + "'s turn after previous human move. Waiting for input.");
            }


            finalReply.status.type = "move_ack";
            finalReply.status.msg = "Move processed successfully.";
            Player finalCurrentPlayer = game.getCurrentTurn();
            finalReply.status.current_move = getPlayerName(finalCurrentPlayer.getPlayerId());
            finalReply.status.id = finalCurrentPlayer.getPlayerId();
            if (wasCapture) {
            finalReply.status.capturedSquare = List.of(middleRow, middleCol);
            }
            return finalReply;
        }
    }

    // Handle resignation
    public UserEventReply handleResign(UserEvent event) {
        System.out.println("[DEBUG] Player " + event.id + " resigned.");

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        Game game = gamePageController.returnGame(event.id);
        if (game == null || !game.gameActive()) {
            reply.status.type = "error";
            reply.status.msg = "Cannot resign: Game not found or already ended.";
            reply.recipients.add(event.id);
            return reply;
        }

        int player1Id = game.getPlayer1ID();
        int player2Id = game.getPlayer2ID();
        int winnerId = -1;

        if (player1Id == event.id) {
            game.Player1Quit();
            winnerId = player2Id;
        } else if (player2Id == event.id) {
            game.Player2Quit();
            winnerId = player1Id;
        } else {
            reply.status.type = "error";
            reply.status.msg = "Cannot resign: You are not a player in this game.";
            reply.recipients.add(event.id);
            return reply;
        }

        reply.status.type = "game_over";
        reply.status.gameOver = true;
        reply.status.winner = winnerId;
        reply.status.draw = false;
        reply.status.msg = "Game ended: Player " + getPlayerName(event.id) + " resigned. Player " + getPlayerName(winnerId) + " wins!";
        reply.status.game_id = game.gameNumber();


        if (player1Id > 1) reply.recipients.add(player1Id);
        if (player2Id > 1) reply.recipients.add(player2Id);

        gameManager.terminateGame(game.gameNumber());

        return reply;
    }


    // Handle draw offer
    public UserEventReply handleDrawOffer(UserEvent event) {
        System.out.println("[DEBUG DisplayConnector] Player " + event.id + " offered a draw.");

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        Game game = gamePageController.returnGame(event.id);
        if (game == null || !game.gameActive()) {
            reply.status.type = "error";
            reply.status.msg = "Cannot offer draw: Game not found or already ended.";
            reply.recipients.add(event.id);
            return reply;
        }

        reply.status.type = "draw_offer";
        reply.status.player = getPlayerName(event.id);
        reply.status.game_id = game.gameNumber();

        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            for (int id : playerIds) {
                if (id != event.id && id > 1) {
                    reply.recipients.add(id);
                    break;
                }
            }
        }

        if (reply.recipients.isEmpty()) {
            System.out.println("[WARN DisplayConnector] No human opponent found to send draw offer to.");
            UserEventReply errorReply = new UserEventReply();
            errorReply.status = new game_status();
            errorReply.recipients.add(event.id);
            errorReply.status.type = "error";
            errorReply.status.msg = "Could not send draw offer (no human opponent found).";
            return errorReply;
        }

        UserEventReply ackReply = new UserEventReply();
        ackReply.status = new game_status();
        ackReply.recipients.add(event.id);
        ackReply.status.type = "info";
        ackReply.status.msg = "Draw offer sent to opponent.";
        appServer.queueMessage(ackReply);


        return reply;
    }


    public UserEventReply handleDrawAccept(UserEvent event) {
        System.out.println("[DEBUG DisplayConnector] Handling draw acceptance from player " + event.id);
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        Game game = gamePageController.returnGame(event.id);
        if (game == null || !game.gameActive()) {
            reply.status.type = "error";
            reply.status.msg = "Cannot accept draw: Game not found or already ended.";
            reply.recipients.add(event.id);
            return reply;
        }

        game.GameDeclareDraw();
        reply.status.type = "game_over";
        reply.status.gameOver = true;
        reply.status.draw = true;
        reply.status.winner = null;
        reply.status.msg = "Game ended: Draw accepted by Player " + getPlayerName(event.id) + "!";
        reply.status.game_id = game.gameNumber();


        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            if (playerIds[0] > 1) reply.recipients.add(playerIds[0]);
            if (playerIds[1] > 1) reply.recipients.add(playerIds[1]);
        }

        gameManager.terminateGame(game.gameNumber());

        return reply;
    }

    // Get allowed moves
    public UserEventReply handleGetAllowedMoves(UserEvent event) {
        System.out.println("[DEBUG] Getting allowed moves for square " + Arrays.toString(event.square) + " from player " + getPlayerName(event.id));

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>(Collections.singletonList(event.id));
        reply.status.type = "valid_moves";
        Game game = gamePageController.returnGame(event.id);
        if (game == null) {
            reply.status.type = "error";
            reply.status.msg = "Game not found.";
            reply.status.legal_moves = new ArrayList<>();
            return reply;
        }

        Player currentTurnPlayer = game.getCurrentTurn();
        if (currentTurnPlayer == null || currentTurnPlayer.getPlayerId() != event.id) {
            reply.status.type = "error";
            reply.status.msg = "Not your turn.";
            reply.status.legal_moves = new ArrayList<>();
            return reply;
        }
        if (event.square == null || event.square.length != 2) {
            reply.status.type = "error";
            reply.status.msg = "Invalid square data provided.";
            reply.status.legal_moves = new ArrayList<>();
            return reply;
        }

        boolean playerColor;
        game.lockBoard();
        try {
            if (game.getPlayer1ID() == event.id) playerColor = game.getPlayer1Color();
            else if (game.getPlayer2ID() == event.id) playerColor = game.getPlayer2Color();
            else {
                reply.status.type = "error";
                reply.status.msg = "Could not determine your color in this game.";
                reply.status.legal_moves = new ArrayList<>();
                return reply;
            }

            Moves movesForPiece = rules.getMovesForSquare(game.getBoard(), playerColor, event.square);

            List<List<Integer>> legalMovesList = new ArrayList<>();
            if (movesForPiece != null && movesForPiece.getMoves() != null) {
                for (Move move : movesForPiece.getMoves()) {
                    if (move != null && move.getDest() != null) {
                        Square destSquare = move.getDest();
                        legalMovesList.add(List.of(destSquare.getRow(), destSquare.getCol()));
                    }
                }
            }

            System.out.println("[DEBUG DisplayConnector] Found " + legalMovesList.size() + " legal moves for " + Arrays.toString(event.square));
            reply.status.legal_moves = legalMovesList;

        } finally {
            game.unlockBoard();
        }

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
            reply.status.starting_player = "Unknown";
        }

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
