package uta.cse3310.PageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Collections;
import uta.cse3310.DB.DB;
import uta.cse3310.DB.Validate;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.GamePageController;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.PairUp.PairUp;
import uta.cse3310.App;
import uta.cse3310.GamePlay.rules;
import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Move;

public class PageManager {
    DB db;
    private PairUp pairUp;
    public NewAcctLogin accountHandler;
    private GameDisplayConnector displayConnector;
    private GameManagerSubsys gameManagerSubsys;
    private GamePageController gamePageController;
    private App appServer;
    private GameManager gameManager;
    private GamePlay gp;

    Map<String, List<Integer>> gamePlayers = new HashMap<>(); // key = gameId, value = player IDs

    private final JoinGameHandler joinGameHandler;

    public PageManager(App appServer) {
        this.appServer = appServer;
        db = new DB();
        DB.createTable();

        this.gp = new GamePlay();
        // Create GameManager and pass it into both controllers
        this.gameManager = new GameManager();
        gameManager.initializeGames();
        gamePageController = new GamePageController(gameManager);
        gameManagerSubsys = new GameManagerSubsys(gamePageController);
        GameTermination gameTermination = new GameTermination();

        displayConnector = new GameDisplayConnector(gamePageController, gameTermination, this.gameManager, this.appServer);

        accountHandler = new NewAcctLogin();
        this.pairUp = new PairUp(this);
        this.joinGameHandler = new JoinGameHandler(this.pairUp);

        System.out.println("PageManager components initialized.");
    }


    // Add a new player to matchmaking
    public void handleNewPlayer(long timestamp, int clientId, String playerName, boolean playAgainstBot, int wins) {
        pairUp.AddPlayer(timestamp, clientId, playerName, playAgainstBot, wins);
    }

    // Remove a player from matchmaking
    public void handlePlayerRemoval(int clientId) {
        pairUp.removePlayer(clientId);
    }


    public GameManager getGameManager() {
        return gameManager;
    }

    // Create a new bot vs bot game and send the board
    public UserEventReply handleBotVsBotRequest(int creatorId) {
        System.out.println("[DEBUG] Handling Bot vs Bot request for creatorID: " + creatorId);

        pairUp.createBotGame(creatorId);
        UserEventReply confirmationReply = new UserEventReply();
        int gameId = creatorId; // gameId is the same as creatorId for bot games

        return displayConnector.sendBotVsBotBoard(creatorId, gameId);
    }

    // Username validation logic
    public JsonObject handleUsernameValidation(String username)
    {
        //using the DB validation:
        JsonObject responseMsg = new JsonObject();
        responseMsg.addProperty("type", "username_status");

        DB.createTable();

        if(accountHandler.usernameExists(username))
        {
            responseMsg.addProperty("type", "username_status");
            responseMsg.addProperty("accepted", false);
        }
        else
        {
            try
            {
                //using DB validation
                Validate.ValidateUser(username);

                //Making sure the username was added
                responseMsg.addProperty("type","username_status");
                responseMsg.addProperty("accepted",true);
            }
            catch(Exception e)
            {
                //Error handling
                System.out.println("Could not validate the username: " + e.getMessage());
                responseMsg.addProperty("type","username_status");
                responseMsg.addProperty("accepted",false);
            }
        }
        return responseMsg;
    }

    // Main handler for all events sent from frontend
    public UserEventReply ProcessInput(UserEvent U) {
        UserEventReply ret = new UserEventReply();
        ret.status = new game_status();
        ret.recipients = new ArrayList<>();

        switch (U.type) {
            case "join": {
                System.out.println("Received username: " + U.playerName);

                // Wrap username into JSON format for compatibility
                JsonObject input = new JsonObject();
                input.addProperty("username", U.playerName);

                // Call the login handler (returns a JSON string)
                JsonObject validationResult = this.handleUsernameValidation(U.playerName);

                // Parse the string response into a JsonObject
                JsonObject parsed = validationResult;

                // Populate the game_status reply
                boolean accepted = parsed.get("accepted").getAsBoolean();
                ret.status.Status = accepted ? "Success" : "Failure";
                ret.status.playerName = U.playerName;
                ret.status.Message = accepted ? "Username accepted." : "Username already exists or is invalid.";
                ret.status.clientId = U.id;

                break;
            }
            case "test": {
                return displayConnector.sendShowGameDisplayTest(U);
            }

            case "join_game": {
                Map<String, String> joinData = new HashMap<>();
                joinData.put("clientId", String.valueOf(U.id));
                joinData.put("gameMode", U.msg);

                if (U.msg != null && U.msg.equalsIgnoreCase("Spectate")) {
                    joinData.put("gameMode", "BotvBot");
                }

                JoinGameHandler.Result result = joinGameHandler.processJoinGame(joinData);

                if (result.isBotvBot) {
                    System.out.println("[DEBUG] BotvBot match requested. Creating bot game...");
                    return handleBotVsBotRequest(result.clientId);
                } else if (result.playAgainstBot) {
                    System.out.println("[DEBUG] Player vs Bot match requested. Sending show_game_display...");
                    // handles in matchmaking
                    // return displayConnector.sendShowGameDisplay(result.clientId);
                    game_status feedback = joinGameHandler.createGameStatusMessage(result.clientId, result.playAgainstBot);
                    ret.status.type = feedback.type;
                    ret.status.msg = feedback.msg;
                } else {
                    game_status feedback = joinGameHandler.createGameStatusMessage(result.clientId, result.playAgainstBot);
                    ret.status.type = feedback.type;
                    ret.status.msg = feedback.msg;
                }
                break;
            }

            case "cancel": {
                System.out.println("Received cancel request from clientId: " + U.id);
                handlePlayerRemoval(U.id);  // Removes the player from matchmaking
                ret.status.type = "cancel_status";
                ret.status.msg = "cancelled";
                break;
            }
            case "resign": {
                System.out.println("[DEBUG] Handling resign request from player: " + U.id);
                ret = displayConnector.handleResign(U);
                if (ret != null && !ret.recipients.isEmpty()) {
                        appServer.queueMessage(ret);
                }
                ret = new UserEventReply();
                ret.recipients = new ArrayList<>();
                break;
            }
            case "draw": {
                System.out.println("[DEBUG] Handling draw offer request from player: " + U.id);
                ret = displayConnector.handleDrawOffer(U);
                if (ret != null && !ret.recipients.isEmpty()) {
                    appServer.queueMessage(ret);
                }
                ret = new UserEventReply();
                ret.recipients = new ArrayList<>();
                break;
            }
            case "draw_accept": {
                System.out.println("[DEBUG] Handling draw accept request from player: " + U.id);
                ret = displayConnector.handleDrawAccept(U);
                if (ret != null && !ret.recipients.isEmpty()) {
                    appServer.queueMessage(ret);
                }
                ret = new UserEventReply();
                ret.recipients = new ArrayList<>();
                break;
            }
            case "get_allowed_moves": {
                System.out.println("[DEBUG] Handling get_allowed_moves from player: " + U.id);
                return displayConnector.handleGetAllowedMoves(U);
            }

            case "game_status": {
                ret.status = gameManagerSubsys.getGameInfo(U.id);
                break;
            }

            case "move": {
                System.out.println("[DEBUG] Handling move request from player: " + U.id);
                // the other player made a move, so we need to update the game state (eg: player 1 made a move so now we notify it to player 2 so his game board can be updated)
                return displayConnector.handleMoveRequest(U);
            }
            default: {
                ret.status.msg = "[WARN] Unrecognized event type: " + U.type;
                break;
            }
        }

        // Always send a response back to the sender
        ret.recipients.add(U.id);
        return ret;
    }

    public void triggerGameDisplay(int gameId, int player1Id, int player2Id) {
        if (player1Id > 1) {
            // 0 and 1 are bots
            UserEventReply reply1 = displayConnector.sendShowGameDisplay(player1Id);
            appServer.queueMessage(reply1); 
        }

        if (player2Id > 1) {
            UserEventReply reply2 = displayConnector.sendShowGameDisplay(player2Id);
            appServer.queueMessage(reply2);
        }
        Game game = (player1Id <= 1 && player2Id <= 1)
        ? gameManager.findGameById(gameId)
        : gameManager.findGameByPlayerId(player1Id > 1 ? player1Id : player2Id);


        if (game != null && game.gameActive()) {
            Player currentPlayer = game.getCurrentTurn();
            boolean isBotTurn = (currentPlayer.getPlayerId() == 0 || currentPlayer.getPlayerId() == 1);

            if (isBotTurn) {
                System.out.println("[DEBUG PageManager] Initial turn detected for Bot " + currentPlayer.getPlayerId() + " in game " + game.gameNumber() + ". Triggering first move.");

                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                Moves botMovesList = gameManager.requestBotMoves(game, currentPlayer.getPlayerId());
                boolean moveSuccessfullyExecuted = false; // Flag

                if (botMovesList != null && botMovesList.size() > 0) {
                    System.out.println("[DEBUG PageManager] Received " + botMovesList.size() + " potential moves from Bot " + currentPlayer.getPlayerId() + ". Attempting in order...");

                    for (Move currentAttempt : botMovesList.getMoves()) {
                        System.out.println("[DEBUG PageManager] Attempting bot move: " + currentAttempt.getStart().getRow()+","+currentAttempt.getStart().getCol()+" -> "+currentAttempt.getDest().getRow()+","+currentAttempt.getDest().getCol());

                        if (!rules.canMovePiece(game, currentAttempt)) {
                            System.out.println("[WARN PageManager] Bot's move attempt is illegal according to rules.canMovePiece. Trying next.");
                            continue;
                        }

                        boolean executed = gp.processAndExecuteMove(game, currentAttempt);

                        if (executed) {
                            System.out.println("[DEBUG PageManager] Bot move executed successfully.");
                            moveSuccessfullyExecuted = true;

                            Player winner = game.getWinner();
                            boolean draw = game.checkDrawCondition();

                            UserEventReply botMoveNotification = new UserEventReply();
                            botMoveNotification.status = new game_status();
                            botMoveNotification.recipients = new ArrayList<>();
                            if (player1Id > 1) botMoveNotification.recipients.add(player1Id);
                            if (player2Id > 1) botMoveNotification.recipients.add(player2Id);


                            botMoveNotification.status.game_id = game.gameNumber();
                            botMoveNotification.status.player = displayConnector.getPlayerName(currentPlayer.getPlayerId()); // Bot's name
                            botMoveNotification.status.from = List.of(currentAttempt.getStart().getRow(), currentAttempt.getStart().getCol());
                            botMoveNotification.status.to = List.of(currentAttempt.getDest().getRow(), currentAttempt.getDest().getCol());


                            if (winner != null || draw) {
                                botMoveNotification.status.type = "game_over";
                                botMoveNotification.status.gameOver = true;
                                botMoveNotification.status.winner = (winner != null) ? winner.getPlayerId() : null;
                                botMoveNotification.status.draw = draw;
                                botMoveNotification.status.msg = draw ? "Game ended in a draw!" : "Player " + displayConnector.getPlayerName(winner.getPlayerId()) + " wins!";
                                game.endGame();
                                System.out.println("[DEBUG PageManager] Game over after initial bot move.");
                            } else {
                                game.switchTurn();
                                Player nextPlayer = game.getCurrentTurn();
                                botMoveNotification.status.type = "move_made_by_other_player_or_bot";
                                botMoveNotification.status.current_move = displayConnector.getPlayerName(nextPlayer.getPlayerId());
                                botMoveNotification.status.id = nextPlayer.getPlayerId();
                                System.out.println("[DEBUG PageManager] Switched turn to " + nextPlayer.getPlayerId() + " after initial bot move.");
                            }

                            appServer.queueMessage(botMoveNotification);
                            System.out.println("[DEBUG PageManager] Queued notification about successful bot move.");

                            break;

                        } else {
                            System.err.println("[ERROR PageManager] gp.processAndExecuteMove failed for bot move: ("+ currentAttempt.getStart().getRow()+","+currentAttempt.getStart().getCol()+" -> "+currentAttempt.getDest().getRow()+","+currentAttempt.getDest().getCol() + "), although rules.canMovePiece passed.");
                            continue;
                        }
                    }

                } else {
                    System.out.println("[WARN PageManager] Initial bot turn, but Bot " + currentPlayer.getPlayerId() + " returned no moves list.");
                    if (game.checkDrawCondition()) {
                        UserEventReply drawNotification = new UserEventReply();
                        drawNotification.status = new game_status();
                        drawNotification.recipients = new ArrayList<>();
                        if (player1Id > 1) drawNotification.recipients.add(player1Id);
                        if (player2Id > 1) drawNotification.recipients.add(player2Id);
                        drawNotification.status.type = "game_over";
                        drawNotification.status.gameOver = true;
                        drawNotification.status.draw = true;
                        drawNotification.status.msg = "Game ended in a draw (Bot has no moves)!";
                        appServer.queueMessage(drawNotification);
                        game.endGame();
                    }
                }

                if (!moveSuccessfullyExecuted && botMovesList != null && botMovesList.size() > 0) {
                    System.err.println("[ERROR PageManager] Bot " + currentPlayer.getPlayerId() + " failed to make any valid move from its provided list.");
                    if (!game.isDraw() && game.checkDrawCondition()) {
                        UserEventReply drawNotification = new UserEventReply();
                        appServer.queueMessage(drawNotification);
                        game.endGame();
                    } else if (!game.isDraw()) {
                        System.err.println("[ERROR PageManager] Bot could not move, forcing draw.");
                        game.GameDeclareDraw();
                    }
                }
            } else {
                System.out.println("[DEBUG PageManager] Initial turn belongs to human player " + currentPlayer.getPlayerId() + ". Waiting for their move.");
            }
        } else {
            System.out.println("[WARN PageManager] Could not find game or game inactive when checking for initial bot move trigger.");
        }
    }
}
