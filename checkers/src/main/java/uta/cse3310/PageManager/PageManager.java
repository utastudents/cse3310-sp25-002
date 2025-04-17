package uta.cse3310.PageManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import uta.cse3310.DB.DB;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.PairUp.PairUp;

public class PageManager {
    DB db;
    PairUp pu;
    public NewAcctLogin accountHandler;
    private GameDisplayConnector displayConnector;
    Integer turn = 0;

    Map<String, List<Integer>> gamePlayers = new HashMap<>(); // key = gameId, value = player IDs

    private final PairUp pairUp = new PairUp();

    public PageManager() {
        db = new DB();
        pu = new PairUp();

        displayConnector = new GameDisplayConnector(new GameManager());

        try
        {
            //temporary url for database for now
            String sqlURL = "jdbc:sqlite:checkers.db";
            //from the documentation, drivermanager is able to get the connection
            //via the url
            Connection connection = DriverManager.getConnection(sqlURL);
            //if successful it should make this connection to use
            accountHandler = new NewAcctLogin(connection);
        }
        catch(SQLException e)
        {
            //if not, then error handle it
            System.out.println("Fail: No database connection: " + e.getMessage());
        }
    }

    // Add a new player to matchmaking
    public void handleNewPlayer(long timestamp, int clientId, String playerName, boolean playAgainstBot, int wins) {
        pairUp.AddPlayer(timestamp, clientId, playerName, playAgainstBot, wins);
    }

    // Remove a player from matchmaking
    public void handlePlayerRemoval(int clientId) {
        pairUp.removePlayer(clientId);
    }

    // Username validation logic
    public JsonObject handleUsernameValidation(String username) {
        JsonObject responseMsg = new JsonObject();

        if (accountHandler.usernameExists(username)) {
            responseMsg.addProperty("type", "username_status");
            responseMsg.addProperty("accepted", false);
        } else if (accountHandler.addUser(username)) {
            responseMsg.addProperty("type", "username_status");
            responseMsg.addProperty("accepted", true);
        } else {
            responseMsg.addProperty("type", "username_status");
            responseMsg.addProperty("accepted", false);
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
                JsonObject result = handleUsernameValidation(U.playerName);
                ret.status.type = result.get("type").getAsString();
                ret.status.msg = result.get("accepted").getAsBoolean() ? "accepted" : "rejected";
                break;
            }

            case "test": {
                return displayConnector.sendShowGameDisplayTest(U);
            }

            case "join_game": {
                Map<String, String> joinData = new HashMap<>();
                joinData.put("ClientID", String.valueOf(U.id));
                joinData.put("gameMode", U.gameMode);

                JoinGameHandler.Result result = joinGameHandler.processJoinGame(joinData);
                game_status feedback = joinGameHandler.createGameStatusMessage(result.clientID, result.playAgainstBot);

                ret.status.type = feedback.type;
                ret.status.msg = feedback.msg;
                break;
            }

            case "cancel": {
                System.out.println("Received cancel request from ClientID: " + U.id);
                handlePlayerRemoval(U.id);  // Removes the player from matchmaking
                ret.status.type = "cancel_status";
                ret.status.msg = "cancelled";
                break;
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
}
