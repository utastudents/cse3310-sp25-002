package uta.cse3310.PageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import uta.cse3310.DB.DB;
import uta.cse3310.PairUp.PairUp;
import uta.cse3310.PageManager.UserEvent;
import uta.cse3310.PageManager.UserEventReply; 
import uta.cse3310.PageManager.game_status;
import uta.cse3310.GameManager.GameManager;
import uta.cse3310.PageManager.GameDisplayConnector;

public class PageManager {
    DB db;
    PairUp pu;
    Integer turn = 0; // TODO: need to call this from GameManager
    GameDisplayConnector gdc = new GameDisplayConnector(new GameManager());


    Map<String, List<Integer>> gamePlayers = new HashMap<>(); // this will store the game players for each game session, key is the game id, value is a list of player ids

   
    // To Use this method globally to parse and convert message please look at the example
     

    public class JSONConverter {

    private static final Gson gson = new Gson();

    public static <T> T parseJson(String jsonString, Class<T> target) 
    {
        return gson.fromJson(jsonString, target);
    }

    /*
    * 
     * EXAMPLE USE
     * 
     * UserEvent u = JSONConverter.parseJson(message, UserEvent.class);
     * PlayerEntry p = JSONConverter.parseJson(message2, PlayerEntry.class);
     * 
     */


    public static String convertObjectToJson(Object obj) 
    {
        return gson.toJson(obj);
    }

        /*
        *
        *EXAMPLE USE 
        *
        *String json = JSONConverter.convertObjectToJson(reply);
        *System.out.println(json);
        *
        */
        

    }


     // ------------------------------------------------------------------------
    // PAIR UP SUBSYSTEM
    // ------------------------------------------------------------------------

    /**
     * Handles initial user requests for matchmaking.
     *
     * @param  string from frontend containing user info
     * @return JSON response with match info or error
     */

    
    private final PairUp pairUp = new PairUp();

    public void handleNewPlayer(long timestamp, String ClientId, String UserName, boolean playAgainstBot, int wins) 
    {
        pairUp.AddPlayer(timestamp, ClientId, UserName, playAgainstBot, wins);
    }
    
    public void handlePlayerRemoval(String ClientId) 
    {
        pairUp.removePlayer(ClientId);
    }
    
   public UserEventReply ProcessInput(UserEvent U) {
        switch (U.type) {
            case "move":
                return gdc.handleMoveRequest(U);
            case "resign":
                return gdc.handleResign(U);
            case "draw":
                return gdc.handleDrawOffer(U);
            case "get_allowed_moves":
                return gdc.handleGetAllowedMoves(U);
            default:
                UserEventReply fallback = new UserEventReply();
                fallback.status = new game_status();
                fallback.status.msg = "Unknown event type: " + U.type;
                fallback.recipients = new ArrayList<>();
                fallback.recipients.add(U.id);
                return fallback;
        }
    }

    public PageManager() {
        db = new DB();
        // pass over a pointer to the single database object in this system
        pu = new PairUp();
    }

}