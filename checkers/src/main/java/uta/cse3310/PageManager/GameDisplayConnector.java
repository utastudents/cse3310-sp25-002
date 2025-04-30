package uta.cse3310.PageManager;

import uta.cse3310.GameManager.GamePageController;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameTermination.GameTermination;
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
import java.util.HashMap;
import uta.cse3310.GamePlay.rules;
import com.google.gson.Gson;


public class GameDisplayConnector {

    private GamePageController gamePageController;
    private GameTermination gameTermination;
    private GameManager gameManager;
    private GamePlay gp;
    private App appServer;
    private Gson gson = new Gson();


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

    private void safeQueueMessage(UserEventReply reply) {
        if (reply != null && reply.recipients != null && !reply.recipients.isEmpty()) {
            appServer.queueMessage(reply);
        } else {
            System.out.println("[WARN DisplayConnector] Attempted to queue invalid or empty recipient reply.");
        }
    }

    public UserEventReply handleMoveRequest(UserEvent event) {
        System.out.println("[DEBUG DisplayConnector] Handling move request from player " + event.id);

        UserEventReply finalReply = new UserEventReply();
        finalReply.status = new game_status();
        finalReply.recipients = new ArrayList<>(Collections.singletonList(event.id));

        Game game = gamePageController.returnGame(event.id);

        if (game == null) {
            finalReply.status.type = "error";
            finalReply.status.msg = "Error: Game not found.";
            System.err.println("[ERROR DisplayConnector] Game not found for player ID: " + event.id);
            return finalReply;
        }
        if (!game.gameActive()) {
            finalReply.status.type = "error";
            finalReply.status.msg = "Error: Game is not active.";
                System.err.println("[ERROR DisplayConnector] Game " + game.gameNumber() + " is not active for player ID: " + event.id);
            return finalReply;
        }
        Player currentTurnPlayer = game.getCurrentTurn();
        if (currentTurnPlayer == null || currentTurnPlayer.getPlayerId() != event.id) {
            finalReply.status.type = "error";
            finalReply.status.msg = "Error: It's not your turn.";
            System.err.println("[ERROR DisplayConnector] Not player " + event.id + "'s turn in game " + game.gameNumber() + ". Current turn: " + (currentTurnPlayer != null ? currentTurnPlayer.getPlayerId() : "null"));
            return finalReply;
        }
        if (event.from == null || event.to == null || event.from.length != 2 || event.to.length != 2) {
            finalReply.status.type = "error";
            finalReply.status.msg = "Error: Invalid move coordinates provided.";
            System.err.println("[ERROR DisplayConnector] Invalid coordinates from player " + event.id + " in game " + game.gameNumber() + ". From: " + Arrays.toString(event.from) + ", To: " + Arrays.toString(event.to));
            return finalReply;
        }

        Square startSquare = game.getBoard().getSquare(event.from[0], event.from[1]);
        Square destSquare = game.getBoard().getSquare(event.to[0], event.to[1]);
        if (startSquare == null || destSquare == null) {
                finalReply.status.type = "error";
                finalReply.status.msg = "Error: Invalid start or destination square.";
                System.err.println("[ERROR DisplayConnector] Null start/dest square for move in game " + game.gameNumber() + ". From: " + Arrays.toString(event.from) + ", To: " + Arrays.toString(event.to));
                return finalReply;
        }
        Move humanPlayerMoveStep = new Move(startSquare, destSquare);

        boolean humanMoveExecuted = false;
        boolean humanWasCapture = false;
        int humanMiddleRow = -1, humanMiddleCol = -1;
        boolean humanFurtherCaptureAvailable = false;
        int opponentId = (game.getPlayer1ID() == event.id) ? game.getPlayer2ID() : game.getPlayer1ID();
        boolean botMoved = false;
        boolean botMadeCapture = false;
        int botCaptureRow = -1, botCaptureCol = -1;
        Move successfulBotMove = null;

        game.lockBoard();
        System.out.println("[DEBUG DisplayConnector] Board locked for human move processing (Player " + event.id + ")");
        try {
                System.out.println("[DEBUG DisplayConnector] Validating human move: (" + event.from[0] + "," + event.from[1] + ") -> (" + event.to[0] + "," + event.to[1] + ") for Player " + event.id);
                if (rules.canMovePiece(game, humanPlayerMoveStep)) {
                System.out.println("[DEBUG DisplayConnector] Human move validation PASSED.");
                    humanMoveExecuted = gp.processAndExecuteMove(game, humanPlayerMoveStep);

                    if (humanMoveExecuted) {
                    System.out.println("[DEBUG DisplayConnector] Human move step executed successfully by player " + event.id);
                    humanWasCapture = rules.isCapture(humanPlayerMoveStep, game.getBoard());
                    if (humanWasCapture) {
                        humanMiddleRow = (event.from[0] + event.to[0]) / 2;
                        humanMiddleCol = (event.from[1] + event.to[1]) / 2;
                        System.out.println("[DEBUG DisplayConnector] Human move was a capture. Captured square approx: [" + humanMiddleRow + ", " + humanMiddleCol + "]");

                        Square landingSquare = game.getBoard().getSquare(event.to[0], event.to[1]);
                        if (landingSquare != null) {
                            humanFurtherCaptureAvailable = rules.isCaptureAvailableFromSquare(game, landingSquare);
                            System.out.println("[DEBUG DisplayConnector] Further capture available for human from ("+landingSquare.getRow()+","+landingSquare.getCol()+"): " + humanFurtherCaptureAvailable);
                        } else {
                            System.err.println("[ERROR DisplayConnector] Landing square null after human move execution, cannot check for further captures.");
                        }
                    } else {
                            System.out.println("[DEBUG DisplayConnector] Human move was not a capture.");
                    }
                } else {
                        System.err.println("[ERROR DisplayConnector] Human move failed execution despite passing validation for player " + event.id);
                        finalReply.status.type = "error";
                        finalReply.status.msg = "Move execution failed unexpectedly.";
                        game.unlockBoard();
                        System.out.println("[DEBUG DisplayConnector] Board unlocked after human move processing failure (Player " + event.id + ")");
                        return finalReply;
                }
            } else {
                System.out.println("[WARN DisplayConnector] Illegal human move step attempted by player " + event.id);
                finalReply.status.type = "error";
                finalReply.status.msg = "Illegal move attempt.";
                game.unlockBoard();
                System.out.println("[DEBUG DisplayConnector] Board unlocked after illegal human move attempt (Player " + event.id + ")");
                return finalReply;
            }
        } finally {
            if (game.boardLock.isHeldByCurrentThread()) {
                    game.unlockBoard();
                    System.out.println("[DEBUG DisplayConnector] Board unlocked after human move processing (Player " + event.id + ")");
            }
        }

        if (humanFurtherCaptureAvailable) {
            System.out.println("[DEBUG DisplayConnector] Further jump required. Turn stays with Player " + event.id);

            UserEventReply continueTurnNotification = new UserEventReply();
            continueTurnNotification.status = new game_status();
            continueTurnNotification.recipients = new ArrayList<>();
            if (game.getPlayer1ID() > 1) continueTurnNotification.recipients.add(game.getPlayer1ID());
            if (game.getPlayer2ID() > 1) continueTurnNotification.recipients.add(game.getPlayer2ID());

            continueTurnNotification.status.type = "continue_turn";
            continueTurnNotification.status.game_id = game.gameNumber();
            continueTurnNotification.status.player = getPlayerName(event.id);
            continueTurnNotification.status.from = List.of(event.from[0], event.from[1]);
            continueTurnNotification.status.to = List.of(event.to[0], event.to[1]);
            if (humanWasCapture) {
                continueTurnNotification.status.capturedSquare = List.of(humanMiddleRow, humanMiddleCol);
                    System.out.println("[DEBUG DisplayConnector] Added capturedSquare ["+humanMiddleRow+","+humanMiddleCol+"] to continue_turn notification.");
            }
            continueTurnNotification.status.current_move = getPlayerName(event.id);
            continueTurnNotification.status.id = event.id;
            continueTurnNotification.status.msg = "Capture made. You must complete the jump from ("+event.to[0]+","+event.to[1]+").";

            safeQueueMessage(continueTurnNotification);

            finalReply.status.type = "move_ack_continue";
            finalReply.status.msg = "First jump successful. Complete your move.";
            if (humanWasCapture) {
                finalReply.status.capturedSquare = List.of(humanMiddleRow, humanMiddleCol);
                    System.out.println("[DEBUG DisplayConnector] Added capturedSquare ["+humanMiddleRow+","+humanMiddleCol+"] to move_ack_continue reply.");
            }
            finalReply.status.current_move = getPlayerName(event.id);
            finalReply.status.id = event.id;

            return finalReply;

        }
        else {
            System.out.println("[DEBUG DisplayConnector] Human turn ends for Player " + event.id + ". Checking for game over and potentially switching turn.");

            Player winnerAfterHumanMove = game.getWinner();
            boolean drawAfterHumanMove = game.checkDrawCondition();

            if (winnerAfterHumanMove != null || drawAfterHumanMove) {
                System.out.println("[DEBUG DisplayConnector] Game over immediately after player " + event.id + "'s move.");

                UserEventReply gameOverNotification = new UserEventReply();
                gameOverNotification.status = new game_status();
                gameOverNotification.recipients = new ArrayList<>();
                    if (game.getPlayer1ID() > 1) gameOverNotification.recipients.add(game.getPlayer1ID());
                    if (game.getPlayer2ID() > 1) gameOverNotification.recipients.add(game.getPlayer2ID());

                gameOverNotification.status.type = "game_over";
                gameOverNotification.status.gameOver = true;
                gameOverNotification.status.game_id = game.gameNumber();
                gameOverNotification.status.from = List.of(event.from[0], event.from[1]);
                gameOverNotification.status.to = List.of(event.to[0], event.to[1]);
                if (humanWasCapture) {
                    gameOverNotification.status.capturedSquare = List.of(humanMiddleRow, humanMiddleCol);
                    System.out.println("[DEBUG DisplayConnector] Added capturedSquare ["+humanMiddleRow+","+humanMiddleCol+"] to game_over notification (human move ended game).");
                }

                if (drawAfterHumanMove) {
                    gameOverNotification.status.winner = null;
                    gameOverNotification.status.draw = true;
                    gameOverNotification.status.msg = "Game ended: It's a draw!";
                } else {
                    gameOverNotification.status.winner = winnerAfterHumanMove.getPlayerId();
                    gameOverNotification.status.draw = false;
                    gameOverNotification.status.msg = "Game ended: Player " + getPlayerName(winnerAfterHumanMove.getPlayerId()) + " wins!";
                }
                System.out.println("[DEBUG DisplayConnector] Game over state: " + gameOverNotification.status.msg);

                gameManager.terminateGame(game.gameNumber());
                safeQueueMessage(gameOverNotification);

                finalReply.status.type = "move_ack";
                finalReply.status.msg = "Move processed. " + gameOverNotification.status.msg;
                finalReply.status.gameOver = true;
                finalReply.status.winner = gameOverNotification.status.winner;
                finalReply.status.draw = gameOverNotification.status.draw;
                finalReply.status.current_move = "Game Over";
                finalReply.status.id = event.id;
                if (humanWasCapture) {
                    finalReply.status.capturedSquare = List.of(humanMiddleRow, humanMiddleCol);
                    System.out.println("[DEBUG DisplayConnector] Added capturedSquare ["+humanMiddleRow+","+humanMiddleCol+"] to final move_ack (human move ended game).");
                }
                return finalReply;

            }
            else {
                game.switchTurn();
                Player nextPlayer = game.getCurrentTurn();
                System.out.println("[DEBUG DisplayConnector] Turn switched to Player " + nextPlayer.getPlayerId());

                if (opponentId > 1) {
                    UserEventReply notify_opponent = new UserEventReply();
                    notify_opponent.status = new game_status();
                    notify_opponent.recipients = new ArrayList<>(Collections.singletonList(opponentId));
                    notify_opponent.status.game_id = game.gameNumber();
                    notify_opponent.status.player = getPlayerName(event.id);
                    notify_opponent.status.from = List.of(event.from[0], event.from[1]);
                    notify_opponent.status.to = List.of(event.to[0], event.to[1]);
                    notify_opponent.status.type = "move_made_by_other_player_or_bot";
                    notify_opponent.status.current_move = getPlayerName(nextPlayer.getPlayerId());
                    notify_opponent.status.id = nextPlayer.getPlayerId();
                    if (humanWasCapture) {
                        notify_opponent.status.capturedSquare = List.of(humanMiddleRow, humanMiddleCol);
                        System.out.println("[DEBUG DisplayConnector] Added capturedSquare ["+humanMiddleRow+","+humanMiddleCol+"] to notification for human opponent " + opponentId);
                    }
                    safeQueueMessage(notify_opponent);
                    System.out.println("[DEBUG DisplayConnector] Queued move notification to human opponent: " + opponentId);
                }

                boolean isNextTurnBot = (nextPlayer.getPlayerId() == 0 || nextPlayer.getPlayerId() == 1);
                if (isNextTurnBot && game.gameActive()) {
                    System.out.println("[DEBUG DisplayConnector] Bot's turn (Player " + nextPlayer.getPlayerId() + ") after human move. Requesting move...");
                    try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                    Moves botMovesList = gameManager.requestBotMoves(game, nextPlayer.getPlayerId());
                    boolean botMoveExecuted = false;


                    if (botMovesList != null && botMovesList.size() > 0) {
                        for (Move botAttempt : botMovesList.getMoves()) {
                            if (botAttempt == null || botAttempt.getStart() == null || botAttempt.getDest() == null) continue;

                            System.out.println("[DEBUG DisplayConnector] Bot attempting move: ("+botAttempt.getStart().getRow()+","+botAttempt.getStart().getCol()+") -> ("+botAttempt.getDest().getRow()+","+botAttempt.getDest().getCol()+")");

                            game.lockBoard();
                            System.out.println("[DEBUG DisplayConnector] Board locked for bot move processing (Bot " + nextPlayer.getPlayerId() + ")");
                            boolean botCanMove = false;
                            boolean executed = false;
                            try {
                                botCanMove = rules.canMovePiece(game, botAttempt);
                                if (botCanMove) {
                                    System.out.println("[DEBUG DisplayConnector] Bot move validation PASSED.");
                                    executed = gp.processAndExecuteMove(game, botAttempt);
                                    if (executed) {
                                        botMoveExecuted = true;
                                        successfulBotMove = botAttempt;
                                        botMadeCapture = rules.isCapture(successfulBotMove, game.getBoard());
                                        if (botMadeCapture) {
                                            botCaptureRow = (successfulBotMove.getStart().getRow() + successfulBotMove.getDest().getRow()) / 2;
                                            botCaptureCol = (successfulBotMove.getStart().getCol() + successfulBotMove.getDest().getCol()) / 2;
                                            System.out.println("[DEBUG DisplayConnector] Bot capture detected. Captured square approx: [" + botCaptureRow + ", " + botCaptureCol + "]");
                                        }
                                    } else { System.err.println("[ERROR DisplayConnector] Bot move failed execution for move ("+botAttempt.getStart().getRow()+","+botAttempt.getStart().getCol()+" -> "+botAttempt.getDest().getRow()+","+botAttempt.getDest().getCol()+") even though rules said OK"); }
                                } else { System.out.println("[WARN DisplayConnector] Bot attempt ("+botAttempt.getStart().getRow()+","+botAttempt.getStart().getCol()+" -> "+botAttempt.getDest().getRow()+","+botAttempt.getDest().getCol()+") illegal, trying next."); }
                            } finally {
                                game.unlockBoard();
                                System.out.println("[DEBUG DisplayConnector] Board unlocked after bot move processing attempt (Bot " + nextPlayer.getPlayerId() + ")");
                            }

                            if (botMoveExecuted) {
                                botMoved = true;
                                break;
                            }
                        }
                    } else {
                        System.out.println("[WARN DisplayConnector] Bot " + nextPlayer.getPlayerId() + " returned null or empty moves list.");
                    }

                    if (botMoveExecuted) {
                        System.out.println("[DEBUG DisplayConnector] Bot move executed successfully. Checking game state...");
                        Player winnerAfterBotMove = game.getWinner();
                        boolean drawAfterBotMove = game.checkDrawCondition();

                        if (winnerAfterBotMove != null || drawAfterBotMove) {
                            UserEventReply botGameOverNotification = new UserEventReply();
                            botGameOverNotification.status = new game_status();
                            botGameOverNotification.recipients = new ArrayList<>();
                            if (game.getPlayer1ID() > 1) botGameOverNotification.recipients.add(game.getPlayer1ID());
                            if (game.getPlayer2ID() > 1) botGameOverNotification.recipients.add(game.getPlayer2ID());

                            botGameOverNotification.status.type = "game_over";
                            botGameOverNotification.status.gameOver = true;
                            botGameOverNotification.status.game_id = game.gameNumber();
                            botGameOverNotification.status.player = getPlayerName(nextPlayer.getPlayerId());
                            botGameOverNotification.status.from = List.of(successfulBotMove.getStart().getRow(), successfulBotMove.getStart().getCol());
                            botGameOverNotification.status.to = List.of(successfulBotMove.getDest().getRow(), successfulBotMove.getDest().getCol());
                            if (botMadeCapture) {
                                botGameOverNotification.status.capturedSquare = List.of(botCaptureRow, botCaptureCol);
                                System.out.println("[DEBUG DisplayConnector] Added capturedSquare ["+botCaptureRow+","+botCaptureCol+"] to game_over notification (bot move ended game).");
                            }

                            if (drawAfterBotMove) {
                                botGameOverNotification.status.winner = null;
                                botGameOverNotification.status.draw = true;
                                botGameOverNotification.status.msg = "Game ended: It's a draw!";
                            } else {
                                botGameOverNotification.status.winner = winnerAfterBotMove.getPlayerId();
                                botGameOverNotification.status.draw = false;
                                botGameOverNotification.status.msg = "Game ended: Player " + getPlayerName(winnerAfterBotMove.getPlayerId()) + " wins!";
                            }
                            System.out.println("[DEBUG DisplayConnector] Game over state after bot move: " + botGameOverNotification.status.msg);
                            gameManager.terminateGame(game.gameNumber());
                            safeQueueMessage(botGameOverNotification);

                            finalReply.status.type = "move_ack";
                            finalReply.status.msg = "Move processed. " + botGameOverNotification.status.msg;
                            finalReply.status.gameOver = true;
                            finalReply.status.winner = botGameOverNotification.status.winner;
                            finalReply.status.draw = botGameOverNotification.status.draw;
                            finalReply.status.current_move = "Game Over";
                            finalReply.status.id = event.id;
                            if (humanWasCapture) {
                                finalReply.status.capturedSquare = List.of(humanMiddleRow, humanMiddleCol);
                                System.out.println("[DEBUG DisplayConnector] Added capturedSquare ["+humanMiddleRow+","+humanMiddleCol+"] from human move to final move_ack (bot move ended game).");
                            }
                            return finalReply;

                        }
                        else {
                            boolean botFurtherCapture = false;
                            Square botLandingSquare = game.getBoard().getSquare(successfulBotMove.getDest().getRow(), successfulBotMove.getDest().getCol());
                            if (botMadeCapture && botLandingSquare != null) {
                                botFurtherCapture = rules.isCaptureAvailableFromSquare(game, botLandingSquare);
                                System.out.println("[DEBUG DisplayConnector] Further capture available for Bot after its move: " + botFurtherCapture);
                            }

                            UserEventReply botMoveNotification = new UserEventReply();
                            botMoveNotification.status = new game_status();
                            botMoveNotification.recipients = new ArrayList<>();
                            if (game.getPlayer1ID() > 1) botMoveNotification.recipients.add(game.getPlayer1ID());
                            if (game.getPlayer2ID() > 1) botMoveNotification.recipients.add(game.getPlayer2ID());

                            botMoveNotification.status.game_id = game.gameNumber();
                            botMoveNotification.status.player = getPlayerName(nextPlayer.getPlayerId());
                            botMoveNotification.status.from = List.of(successfulBotMove.getStart().getRow(), successfulBotMove.getStart().getCol());
                            botMoveNotification.status.to = List.of(successfulBotMove.getDest().getRow(), successfulBotMove.getDest().getCol());
                            if (botMadeCapture) {
                                botMoveNotification.status.capturedSquare = List.of(botCaptureRow, botCaptureCol);
                                System.out.println("[DEBUG DisplayConnector] Added capturedSquare ["+botCaptureRow+","+botCaptureCol+"] to bot move notification.");
                            }


                            if (botFurtherCapture) {
                                botMoveNotification.status.type = "continue_turn";
                                botMoveNotification.status.current_move = getPlayerName(nextPlayer.getPlayerId());
                                botMoveNotification.status.id = nextPlayer.getPlayerId();
                                botMoveNotification.status.msg = "Bot captured and must continue jump.";
                                System.out.println("[DEBUG DisplayConnector] Bot must continue jump. Turn stays with Bot " + nextPlayer.getPlayerId());

                            } else {
                                game.switchTurn();
                                Player humanPlayerNext = game.getCurrentTurn();
                                botMoveNotification.status.type = "move_made_by_other_player_or_bot";
                                botMoveNotification.status.current_move = getPlayerName(humanPlayerNext.getPlayerId());
                                botMoveNotification.status.id = humanPlayerNext.getPlayerId();
                                System.out.println("[DEBUG DisplayConnector] Bot turn finished. Switched back to Player " + humanPlayerNext.getPlayerId());
                            }

                            safeQueueMessage(botMoveNotification);
                        }
                    }
                    else if (botMovesList != null) {
                        System.err.println("[ERROR DisplayConnector] Bot " + nextPlayer.getPlayerId() + " failed to make any valid move in game " + game.gameNumber());
                        if (game.checkDrawCondition()) {
                            UserEventReply drawNotification = new UserEventReply();
                            drawNotification.status = new game_status();
                            drawNotification.recipients = new ArrayList<>();
                            if (game.getPlayer1ID() > 1) drawNotification.recipients.add(game.getPlayer1ID());
                            if (game.getPlayer2ID() > 1) drawNotification.recipients.add(game.getPlayer2ID());
                            drawNotification.status.type = "game_over";
                            drawNotification.status.gameOver = true;
                            drawNotification.status.draw = true;
                            drawNotification.status.winner = null;
                            drawNotification.status.msg = "Game ended: Draw by stalemate (Bot " + getPlayerName(nextPlayer.getPlayerId()) + " has no valid moves)!";
                            drawNotification.status.game_id = game.gameNumber();

                            gameManager.terminateGame(game.gameNumber());
                            safeQueueMessage(drawNotification);

                            finalReply.status.type = "move_ack";
                            finalReply.status.msg = "Move processed. " + drawNotification.status.msg;
                            finalReply.status.gameOver = true;
                            finalReply.status.winner = null;
                            finalReply.status.draw = true;
                            finalReply.status.current_move = "Game Over";
                            finalReply.status.id = event.id;
                            if (humanWasCapture) {
                                finalReply.status.capturedSquare = List.of(humanMiddleRow, humanMiddleCol);
                            }
                            return finalReply;
                        } else {
                            System.err.println("[CRITICAL DisplayConnector] Bot " + nextPlayer.getPlayerId() + " had no valid moves, but game is not a draw? Investigate game state.");
                        }
                    }
                }

                Player finalCurrentPlayer = game.getCurrentTurn();
                if (finalCurrentPlayer == null) {
                        System.err.println("[CRITICAL DisplayConnector] Current player is null when preparing final move_ack. Game " + game.gameNumber());
                        finalReply.status.type = "error";
                        finalReply.status.msg = "Internal error: Cannot determine next player.";
                        finalReply.status.id = event.id;
                        return finalReply;
                }

                finalReply.status.type = "move_ack";
                finalReply.status.msg = "Move processed successfully.";
                finalReply.status.current_move = getPlayerName(finalCurrentPlayer.getPlayerId());
                finalReply.status.id = finalCurrentPlayer.getPlayerId();
                if (humanWasCapture) {
                    finalReply.status.capturedSquare = List.of(humanMiddleRow, humanMiddleCol);
                    System.out.println("[DEBUG DisplayConnector] Added capturedSquare ["+humanMiddleRow+","+humanMiddleCol+"] to final move_ack for player " + event.id);
                }
                    System.out.println("[DEBUG DisplayConnector] Sending final move_ack to player " + event.id + ". Next turn: " + finalCurrentPlayer.getPlayerId());
                return finalReply;

            }
        }
    }

    // Handle resignation
    public UserEventReply handleResign(UserEvent event) {
        System.out.println("[DEBUG DisplayConnector] Player " + event.id + " resigned.");

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
        int loserId = event.id;

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

        // Ensure game state reflects winner/loser before terminating
        game.endGame(); // Mark game inactive

        reply.status.type = "game_over";
        reply.status.gameOver = true;
        reply.status.winner = winnerId;
        reply.status.loser = loserId; // Added loser ID
        reply.status.draw = false;
        reply.status.msg = "Game ended: Player " + getPlayerName(loserId) + " resigned. Player " + getPlayerName(winnerId) + " wins!";
        reply.status.game_id = game.gameNumber();


        if (player1Id > 1) reply.recipients.add(player1Id); // Notify P1 if human
        if (player2Id > 1) reply.recipients.add(player2Id); // Notify P2 if human

        // Terminate game after setting state and preparing reply
        gameManager.terminateGame(game.gameNumber());
        safeQueueMessage(reply); // Use helper

        // Send simple ack back to resigning player separately if needed,
        // but the game_over message already goes to them.
        return null; // Indicate no further direct reply needed to sender via ProcessInput return
    }


    // Handle draw offer
    public UserEventReply handleDrawOffer(UserEvent event) {
        System.out.println("[DEBUG DisplayConnector] Player " + event.id + " offered a draw.");

        UserEventReply reply = new UserEventReply(); // This will be the offer sent to opponent
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        Game game = gamePageController.returnGame(event.id);
        if (game == null || !game.gameActive()) {
            // Send error back ONLY to sender
            UserEventReply errorReply = new UserEventReply();
            errorReply.status = new game_status();
            errorReply.recipients = new ArrayList<>(Collections.singletonList(event.id));
            errorReply.status.type = "error";
            errorReply.status.msg = "Cannot offer draw: Game not found or already ended.";
            return errorReply;
        }

        // Determine opponent ID
        int player1Id = game.getPlayer1ID();
        int player2Id = game.getPlayer2ID();
        int opponentId = (player1Id == event.id) ? player2Id : player1Id;

        // Prepare the draw offer message FOR THE OPPONENT
        reply.status.type = "draw_offer";
        reply.status.player = getPlayerName(event.id); // Who offered
        reply.status.game_id = game.gameNumber();
        reply.status.msg = getPlayerName(event.id) + " has offered a draw. Do you accept?";

        // Add only the human opponent as recipient
        if (opponentId > 1) { // 0 and 1 are bots
            reply.recipients.add(opponentId);
             System.out.println("[DEBUG DisplayConnector] Sending draw offer to opponent ID: " + opponentId);
             safeQueueMessage(reply); // Queue the offer to the opponent
        } else {
            // If opponent is a bot, automatically reject? Or handle differently?
            // For now, inform the human offerer that bot cannot accept.
            System.out.println("[DEBUG DisplayConnector] Opponent is a bot (ID: "+opponentId+"). Cannot send draw offer.");
            UserEventReply botReply = new UserEventReply();
            botReply.status = new game_status();
            botReply.recipients = new ArrayList<>(Collections.singletonList(event.id)); // Send back to the offerer
            botReply.status.type = "info"; // Use info type for this feedback
            botReply.status.msg = "Cannot offer draw to a bot.";
            return botReply; // Return the info message
        }

        // Send an acknowledgment back to the player who offered the draw
        UserEventReply ackReply = new UserEventReply();
        ackReply.status = new game_status();
        ackReply.recipients.add(event.id); // Only to the offerer
        ackReply.status.type = "info";
        ackReply.status.msg = "Draw offer sent to opponent.";
        safeQueueMessage(ackReply); // Send the acknowledgment via queue

        return null; // Indicate ProcessInput doesn't need to send a direct reply
    }


    public UserEventReply handleDrawAccept(UserEvent event) {
        System.out.println("[DEBUG DisplayConnector] Handling draw acceptance from player " + event.id);
        UserEventReply reply = new UserEventReply(); // This will be the game_over message
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        Game game = gamePageController.returnGame(event.id);
        if (game == null || !game.gameActive()) {
             // Send error back ONLY to sender
            UserEventReply errorReply = new UserEventReply();
            errorReply.status = new game_status();
            errorReply.recipients = new ArrayList<>(Collections.singletonList(event.id));
            errorReply.status.type = "error";
            errorReply.status.msg = "Cannot accept draw: Game not found or already ended.";
            return errorReply;
        }

        // Declare draw and mark game inactive
        game.GameDeclareDraw();

        // Prepare the game over message
        reply.status.type = "game_over";
        reply.status.gameOver = true;
        reply.status.draw = true;
        reply.status.winner = null; // No winner in a draw
        reply.status.loser = null; // No loser in a draw
        reply.status.msg = "Game ended: Draw accepted by Player " + getPlayerName(event.id) + "!";
        reply.status.game_id = game.gameNumber();


        // Add all human players as recipients for the game over message
        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            if (playerIds[0] > 1) reply.recipients.add(playerIds[0]); // Notify P1 if human
            if (playerIds[1] > 1) reply.recipients.add(playerIds[1]); // Notify P2 if human
        }

        // Terminate the game in the game manager
        gameManager.terminateGame(game.gameNumber());
        safeQueueMessage(reply); // Queue the game over message

        return null; // Indicate ProcessInput doesn't need to send a direct reply
    }

    // Get allowed moves
    public UserEventReply handleGetAllowedMoves(UserEvent event) {
        System.out.println("[DEBUG DisplayConnector] Getting allowed moves for square " + Arrays.toString(event.square) + " from player " + getPlayerName(event.id));

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>(Collections.singletonList(event.id));
        reply.status.type = "valid_moves"; // Ensure type is set correctly

        Game game = gamePageController.returnGame(event.id);
        if (game == null) {
            reply.status.type = "error";
            reply.status.msg = "Game not found.";
            reply.status.legal_moves = new ArrayList<>();
             System.err.println("[ERROR DisplayConnector] handleGetAllowedMoves: Game not found for player " + event.id);
            return reply;
        }

        Player currentTurnPlayer = game.getCurrentTurn();
        if (currentTurnPlayer == null || currentTurnPlayer.getPlayerId() != event.id) {
            reply.status.type = "error";
            reply.status.msg = "Not your turn.";
            reply.status.legal_moves = new ArrayList<>();
            System.err.println("[ERROR DisplayConnector] handleGetAllowedMoves: Not player " + event.id + "'s turn in game " + game.gameNumber());
            return reply;
        }
        if (event.square == null || event.square.length != 2) {
            reply.status.type = "error";
            reply.status.msg = "Invalid square data provided.";
            reply.status.legal_moves = new ArrayList<>();
             System.err.println("[ERROR DisplayConnector] handleGetAllowedMoves: Invalid square data from player " + event.id + ": " + Arrays.toString(event.square));
            return reply;
        }

        boolean playerColor;
        List<List<Integer>> legalMovesList = new ArrayList<>();
        game.lockBoard(); // Lock before accessing board state
        System.out.println("[DEBUG DisplayConnector] Board locked for get_allowed_moves (Player " + event.id + ")");
        try {
            // Determine player's color safely
            if (game.getPlayer1ID() == event.id) playerColor = game.getPlayer1Color();
            else if (game.getPlayer2ID() == event.id) playerColor = game.getPlayer2Color();
            else {
                reply.status.type = "error";
                reply.status.msg = "Could not determine your color in this game.";
                reply.status.legal_moves = new ArrayList<>();
                System.err.println("[ERROR DisplayConnector] handleGetAllowedMoves: Could not determine color for player " + event.id + " in game " + game.gameNumber());
                return reply; // Return early (finally block will still run)
            }

            // Get moves using the rules engine
            Moves movesForPiece = rules.getMovesForSquare(game.getBoard(), playerColor, event.square);

            // Format the moves for the frontend
            if (movesForPiece != null && movesForPiece.getMoves() != null) {
                for (Move move : movesForPiece.getMoves()) {
                    if (move != null && move.getDest() != null) {
                        Square destSquare = move.getDest();
                        legalMovesList.add(List.of(destSquare.getRow(), destSquare.getCol()));
                    }
                }
            } else {
                System.out.println("[DEBUG DisplayConnector] handleGetAllowedMoves: rules.getMovesForSquare returned null or empty for " + Arrays.toString(event.square));
            }

            System.out.println("[DEBUG DisplayConnector] handleGetAllowedMoves: Found " + legalMovesList.size() + " legal moves for " + Arrays.toString(event.square));
            reply.status.legal_moves = legalMovesList; // Assign the potentially empty list

        } finally {
            game.unlockBoard(); // Ensure board is unlocked
             System.out.println("[DEBUG DisplayConnector] Board unlocked after get_allowed_moves (Player " + event.id + ")");
        }

        return reply;
    }

    // Send game board to creator/spectator for a Bot vs Bot match
    public UserEventReply sendBotVsBotBoard(int spectatorId, int gameId) {
        System.out.println("[DEBUG DisplayConnector] Sending Bot vs Bot board display info to spectator ID: " + spectatorId + " for game ID: " + gameId);

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>(Collections.singletonList(spectatorId));

        Game game = gameManager.findGameById(gameId); // Use findGameById for BotvBot

        reply.status.type = "show_game_display";
        reply.status.game_id = gameId;
        reply.status.msg = "Spectating Bot vs Bot match.";
        reply.status.player = getPlayerName(spectatorId); // Use spectator's ID for player field
        reply.status.playerId = spectatorId; // Actual ID
        reply.status.clientId = spectatorId; // Also set clientId
        reply.status.player_color = "S"; // Spectator color


        if (game != null && game.getCurrentTurn() != null) {
            reply.status.starting_player = getPlayerName(game.getCurrentTurn().getPlayerId());
        } else {
            reply.status.starting_player = "Bot I"; // Default if game or turn is somehow null
            System.out.println("[WARN DisplayConnector] Could not get starting player for BotVsBot game ID: " + gameId);
        }

        return reply;
    }

    // Send initial game display info to a human player
    public UserEventReply sendShowGameDisplay(int clientId) {
        System.out.println("[DEBUG DisplayConnector] Preparing show_game_display for clientId: " + clientId);

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>(Collections.singletonList(clientId));

        Game game = gamePageController.returnGame(clientId);

        if (game == null) {
            System.err.println("[ERROR DisplayConnector] sendShowGameDisplay: No active game found for client ID " + clientId + ".");
             reply.status.type = "error";
             reply.status.msg = "Could not find your game. Please try joining again.";
             return reply; // Return an error reply
        }

        System.out.println("[DEBUG DisplayConnector] Found active game " + game.gameNumber() + " for clientId " + clientId);

        reply.status.type = "show_game_display";
        reply.status.game_id = game.gameNumber();
        reply.status.player = getPlayerName(clientId); // Player's assigned name/ID
        reply.status.playerId = clientId; // The actual ID
        reply.status.clientId = clientId; // Also set clientId in status

        boolean isPlayer1 = (game.getPlayer1ID() == clientId);
        boolean isPlayer2 = (game.getPlayer2ID() == clientId);
        String playerColor = "S";

        if (isPlayer1) {
            playerColor = game.getPlayer1Color() ? "W" : "B";
        } else if (isPlayer2) {
            playerColor = game.getPlayer2Color() ? "W" : "B";
        } else {
            System.out.println("[WARN DisplayConnector] sendShowGameDisplay: Client ID " + clientId + " not found as P1 or P2 in game " + game.gameNumber() + ". Assigning spectator color.");
        }
        reply.status.player_color = playerColor;
        System.out.println("[DEBUG DisplayConnector] Assigned color " + playerColor + " to client " + clientId);

        // Determine starting player
        Player startingPlayer = game.getCurrentTurn();
        if (startingPlayer != null) {
            reply.status.starting_player = getPlayerName(startingPlayer.getPlayerId());
            System.out.println("[DEBUG DisplayConnector] Starting player for game " + game.gameNumber() + ": " + reply.status.starting_player);
        } else {
            System.err.println("[ERROR DisplayConnector] sendShowGameDisplay: Could not determine starting player for game " + game.gameNumber());
            reply.status.starting_player = "Unknown";
        }

        return reply;
    }

    public UserEventReply sendShowGameDisplayTest(UserEvent event) {
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        reply.status.type = "show_game_display";
        reply.status.game_id = 123;
        reply.status.player = "Luigi (ID: 1)";
        reply.status.player_color = "B";
        reply.status.starting_player = "Mario (ID: 2)";

        reply.recipients.add(1);
        reply.recipients.add(2);

        return reply;
    }

}