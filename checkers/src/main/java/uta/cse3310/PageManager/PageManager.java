package uta.cse3310.PageManager;

import java.util.ArrayList;

import uta.cse3310.DB.DB;
import uta.cse3310.PairUp.PairUp;

public class PageManager {
    DB db;
    PairUp pu;
    Integer turn = 0; // just here for a demo. note: it is a global, effectively and is not unique per client (or game)

    // ------------------------------------------------------------------------
    // Web Socket connection
    // ------------------------------------------------------------------------

    /**
     * establish a channel between front end and back end
     *
     * @param  JSON string from frontend 
     * @return JSON response from back end
     */

     public class WebSocketServer {

        // TO DO: write a code to link WebSocket once the API call has been made

    
    public void OnOpen() {
        // print connection established
    }

    
    public void OnMessage() {

        // receive message in JSON format from HTML
        // send message in JSON format HTML
    }

    public void onClose() {
        // print connection breaked
    }

    public void onError(){
        // print error while setting up connection
    }
}

    // ------------------------------------------------------------------------
    // PAIR UP SUBSYSTEM
    // ------------------------------------------------------------------------

    /**
     * Handles initial user requests for matchmaking.
     *
     * @param json_UI JSON string from frontend containing user info
     * @return JSON response with match info or error
     */
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
    // GAME EVENT HANDLING
    // ------------------------------------------------------------------------

    /**
     * Handles move requests from frontend.
     *
     * @param event The move event from a player
     * @return A reply containing status and recipient info
     */
    public UserEventReply handleMoveRequest(UserEvent event) {
        System.out.println("[DEBUG] Received move request from player " + event.id);
        
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();
        reply.recipients.add(event.id); 
        return reply;
    }

    /**
     * Handles resign events sent from frontend.
     *
     * @param event The resign event from a player
     * @return A reply confirming the resignation
     */
    public UserEventReply handleResign(UserEvent event) {
        System.out.println("[DEBUG] Player " + event.id + " resigned.");

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();
        reply.recipients.add(event.id);
        return reply;
    }

    /**
     * Handles draw offer events sent from frontend.
     *
     * @param event The draw offer event from a player
     * @return A reply confirming the draw offer
     */    
    public UserEventReply handleDrawOffer(UserEvent event) {
        System.out.println("[DEBUG] Player " + event.id + " offered a draw.");

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status(); 
        reply.recipients = new ArrayList<>();
        reply.recipients.add(event.id);
        return reply;
    }

    // /**
    //  * Handles requests for allowed moves from frontend.
    //  *
    //  * @param event The allowed-moves request from a player
    //  * @return A reply with placeholder move data
    //  */
    // public UserEventReply handleGetAllowedMoves(UserEvent event) {
    //     System.out.println("[DEBUG] Requesting allowed moves for piece by player " + event.id);

    //     UserEventReply reply = new UserEventReply();
    //     reply.status = new game_status(); 
    //     reply.recipients = new ArrayList<>();
    //     reply.recipients.add(event.id);
    //     return reply;
    // }

    
    // ------------------------------------------------------------------------
    // DEMO TEST METHOD (can be removed/replaced later)
    // ------------------------------------------------------------------------

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
        pu = new PairUp(db);
    }

}
