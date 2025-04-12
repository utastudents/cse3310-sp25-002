package uta.cse3310.PageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import uta.cse3310.DB.DB;
import uta.cse3310.PairUp.PairUp;

public class PageManager {
    DB db;
    PairUp pu;
    Integer turn = 0; // TODO: need to call this from GameManager


    Map<String, List<Integer>> gamePlayers = new HashMap<>(); // this will store the game players for each game session, key is the game id, value is a list of player ids

    // ------------------------------------------------------------------------
    // PAIR UP SUBSYSTEM
    // ------------------------------------------------------------------------

    /**
     * Handles initial user requests for matchmaking.
     *
     * @param json_UI JSON string from frontend containing user info
     * @return JSON response with match info or error
     */

    /** To Use this method globally to parse and convert message just include:
     * 
     * EXAMPLE USE
     * 
     * UserEvent u = JSONConverter.parseJson(message, UserEvent.class);
     * PlayerEntry p = JSONConverter.parseJson(message2, PlayerEntry.class);
     * 
     */

    public class JSONConverter {

    private static final Gson gson = new Gson();

    public static <T> T parseJson(String jsonString, Class<T> target) 
    {
        return gson.fromJson(jsonString, target);
    }

    public static String convertObjectToJson(Object obj) 
    {
        return gson.toJson(obj);
    }

    }

    
    
    
    public String handleUserReq(String json_UI) {

        //TO DO: PairUp interface should create a method to link data
        /* 
        try {
         
            List<pairup_subsys.PlayerEntry> PlayersWaiting = pairup_subsys.JSONconverter.parsePlayersFromJson(json_UI);
            List<pairup_subsys.reply_m> PlayersActive = pu.pairPlayers(PlayersWaiting);

            return pairup_subsys.JSONconverter.convertRepliesToJson(PlayersActive);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to process player pairing.\"}";
        }
        */
       return "{\"status\": \"PairUp not implemented yet.\"}";
    }

    // ------------------------------------------------------------------------
    // DEMO TEST METHOD (can be removed/replaced later)
    // ------------------------------------------------------------------------
    // TODO : add switch statement for controlling types of events
    /**
     * Placeholder method for testing input/output with the frontend.
     * Simulates switching turns on each call.
     *
     * @param U The user event received
     * @return A test reply with toggled turn
     */
    public UserEventReply ProcessInput(UserEvent U) {
        UserEventReply ret = new UserEventReply();
        ret.status = new game_status();
        // fake data for the example
        if (turn == 0) {
            ret.status.turn = 1;
            turn = 1;
        } else {
            ret.status.turn = 0;
            turn = 0;
        }

        // for now, the idea is to send it back where it came from
        // in the future, all of the id's that need the data will need to
        // be added to this list
        ret.recipients = new ArrayList<>();
        ret.recipients.add(U.id);

        return ret;

    }

    public PageManager() {
        db = new DB();
        // pass over a pointer to the single database object in this system
        pu = new PairUp();
    }

}
