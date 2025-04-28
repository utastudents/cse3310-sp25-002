package uta.cse3310.PageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uta.cse3310.DB.DB;
import uta.cse3310.DB.Validate;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.GamePageController;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.PairUp.PairUp;

public class PageManager {
    DB db;
    PairUp pu;
    public NewAcctLogin accountHandler;
    private GameDisplayConnector displayConnector;
    private GameManagerSubsys gameManagerSubsys;
    private GamePageController gamePageController;
    
    Integer turn = 0;

    Map<String, List<Integer>> gamePlayers = new HashMap<>(); // key = gameId, value = player IDs

    private final PairUp pairUp = new PairUp();

    private final JoinGameHandler joinGameHandler = new JoinGameHandler();

    public PageManager() {
        db = new DB();
        DB.createTable();

        // Create GameManager and pass it into both controllers
        GameManager gameManager = new GameManager();
        gamePageController = new GamePageController(gameManager);
        gameManagerSubsys = new GameManagerSubsys(gamePageController);

        // Create GameTermination and connect it
        GameTermination gameTermination = new GameTermination();
        displayConnector = new GameDisplayConnector(gamePageController, gameTermination);  

        accountHandler = new NewAcctLogin();
    }


    // Add a new player to matchmaking
    public void handleNewPlayer(long timestamp, int clientId, String playerName, boolean playAgainstBot, int wins) {
        pairUp.AddPlayer(timestamp, clientId, playerName, playAgainstBot, wins);
    }

    // Remove a player from matchmaking
    public void handlePlayerRemoval(int clientId) {
        pairUp.removePlayer(clientId);
    }

    // Create a new bot vs bot game and send the board
    public UserEventReply handleBotVsBotRequest(int creatorId) {
        System.out.println("[DEBUG] Handling Bot vs Bot request for creatorID: " + creatorId);

        pairUp.createBotGame(creatorId);

        int gameId = creatorId; // gameId is the same as creatorId for bot games

        return displayConnector.sendBotVsBotBoard(creatorId, gameId);
    }

    // Username validation logic
    public JsonObject handleUsernameValidation(String username)
    {
        //using the DB validation:
        JsonObject responseMsg = new JsonObject();

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
                String loginResult = accountHandler.processUsernameInput(input.toString());

                // Parse the string response into a JsonObject
                JsonObject parsed = JsonParser.parseString(loginResult).getAsJsonObject();

                // Populate the game_status reply
                ret.status.Status = parsed.get("Status").getAsString();
                ret.status.Message = parsed.get("Message").getAsString();
                ret.status.playerName = U.playerName;

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

            default: {
                ret.status.msg = "[WARN] Unrecognized event type: " + U.type;
                break;
            }

            case "game_status": {
                ret.status = gameManagerSubsys.getGameInfo(U.id);
                break;
            }
            
        }

        // Always send a response back to the sender
        ret.recipients.add(U.id);
        return ret;
    }
}
