package uta.cse3310.PageManager;

import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.Move;
import uta.cse3310.PageManager.UserEvent;
import uta.cse3310.PageManager.UserEventReply;
import uta.cse3310.PageManager.game_status;

import java.util.ArrayList;
import java.util.List;

public class GameDisplayConnector {

    private GameManager gameManager;

    public GameDisplayConnector(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    // Handle a move from the front-end
    public UserEventReply handleMoveRequest(UserEvent event) {
        System.out.println("[DEBUG] Received move from player " + event.id);

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        if (event.from != null && event.to != null) {
            Move move = new Move(event.from[0], event.from[1], event.to[0], event.to[1]);
            gameManager.processMove(event.id, move);

            reply.status.type = "move_made_by_other_player_or_bot";
            reply.status.game_id = event.gameId;
            reply.status.player = event.playerName + " (ID: " + event.id + ")";
            reply.status.from = List.of(event.from[0], event.from[1]);
            reply.status.to = List.of(event.to[0], event.to[1]);
        } else {
            reply.status.type = "error";
            reply.status.msg = "Invalid move data.";
        }

        reply.recipients.add(event.id); // You can also add the opponent ID here
        return reply;
    }

    // Handle resignation
    public UserEventReply handleResign(UserEvent event) {
        System.out.println("[DEBUG] Player " + event.id + " resigned.");

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        gameManager.removePlayer(event.id);
        reply.status.type = "resign";
        reply.status.player = event.playerName + " (ID: " + event.id + ")";

        return reply;
    }

    // Handle draw offer (placeholder logic)
    public UserEventReply handleDrawOffer(UserEvent event) {
        System.out.println("[DEBUG] Player " + event.id + " offered a draw.");

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        reply.status.type = "draw_offer";
        reply.status.player = event.playerName + " (ID: " + event.id + ")";

        return reply;
    }

    // (Optional) Get allowed moves — requires GamePlay support
    public UserEventReply handleGetAllowedMoves(UserEvent event) {
        System.out.println("[DEBUG] Getting allowed moves for square " + event.square + " from player " + event.id);

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        reply.status.msg = "Allowed moves logic not yet implemented.";
        reply.recipients.add(event.id);

        return reply;
    }
}
