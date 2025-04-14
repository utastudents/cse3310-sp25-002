
package uta.cse3310.PageManager;

import uta.cse3310.GameManager.GameManager;
import uta.cse3310.PageManager.UserEvent;
import uta.cse3310.PageManager.UserEventReply;
import uta.cse3310.PageManager.game_status;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class GameDisplayConnector {

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

}