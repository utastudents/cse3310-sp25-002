package uta.cse3310.PageManager;

import uta.cse3310.GameManager.GamePageController;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.PageManager.UserEvent;
import uta.cse3310.PageManager.UserEventReply;
import uta.cse3310.PageManager.game_status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Needs from GameManager:
// getAllowedMoves, getGameState, getGameStatus, getGameOver, getWinner, getLoser, getallPlayerIDs 

public class GameDisplayConnector {

    private GamePageController gamePageController;
    private GameTermination gameTermination;

    public GameDisplayConnector(GamePageController gamePageController, GameTermination gameTermination) {
        this.gamePageController = gamePageController;
        this.gameTermination = gameTermination;
    }

    // Handle a move from the front-end
    public UserEventReply handleMoveRequest(UserEvent event) {
        System.out.println("[DEBUG] Received move from player " + event.id);

        // Get client ID from event
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        // Validate game move is valid
        if (event.from != null && event.to != null) {
            Move move = new Move(event.from[0], event.from[1], event.to[0], event.to[1]);
            gamePageController.processMove(event.id, move);

            reply.status.type = "move_made_by_other_player_or_bot";
            reply.status.game_id = event.gameId;
            reply.status.player = event.playerName + " (ID: " + event.id + ")";
            reply.status.from = List.of(event.from[0], event.from[1]);
            reply.status.to = List.of(event.to[0], event.to[1]);
        } else {
            reply.status.type = "error";
            reply.status.msg = "Invalid move data.";
        }

        // Get all player IDs from GameManager
        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            for (int id : playerIds) {
                reply.recipients.add(id);
            }
        }

        return reply;
    }

    // Handle resignation
    public UserEventReply handleResign(UserEvent event) {
        System.out.println("[DEBUG] Player " + event.id + " resigned.");

        // Get client ID from event
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        // Trigger game termination logic
        //gameTermination.terminateGame(event.gameId, event.id);

        reply.status.type = "resign";
        reply.status.player = event.playerName + " (ID: " + event.id + ")";

        // Get all player IDs from GameManager
        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            for (int id : playerIds) {
                reply.recipients.add(id);
            }
        }

        return reply;
    }

    // Handle draw offer 
    public UserEventReply handleDrawOffer(UserEvent event) {
        System.out.println("[DEBUG] Player " + event.id + " offered a draw.");

        // Get client ID from event
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        // Send draw offer to other player
        reply.status.type = "draw_offer";
        reply.status.player = event.playerName + " (ID: " + event.id + ")";

        // Get all player IDs from GameManager
        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            for (int id : playerIds) {
                reply.recipients.add(id);
            }
        }

        return reply;
    }

    // Get allowed moves 
    public UserEventReply handleGetAllowedMoves(UserEvent event) {
        System.out.println("[DEBUG] Getting allowed moves for square " + event.square + " from player " + event.id);

        // Get client ID from event
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        reply.status.type = "valid_moves";

        // Use GamePageController to get legal moves from GamePlay
        Map<Square, Moves> moveMap = gamePageController.getAllowedMoves(event.id, event.square);

        List<List<Integer>> legalMoves = new ArrayList<>();
        if (moveMap != null) {
            for (Square s : moveMap.keySet()) {
                List<Integer> move = new ArrayList<>();
                move.add(s.getRow());
                move.add(s.getCol());
                legalMoves.add(move);
            }
        }

        System.out.println("[DEBUG] moveMap content: " + moveMap);
        System.out.println("[DEBUG] legalMoves size: " + legalMoves.size());

        reply.status.legal_moves = legalMoves;

        // Get all player IDs from GameManager
        int[] playerIds = gamePageController.getAllPlayerIDs(event.id);
        if (playerIds != null) {
            for (int id : playerIds) {
                reply.recipients.add(id);
            }
        }

        return reply;
    }

    // Send game board to creator for a Bot vs Bot match
    public UserEventReply sendBotVsBotBoard(int creatorId, int gameId) {
        System.out.println("[DEBUG] Sending Bot vs Bot board to creator ID: " + creatorId);

        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        reply.status.type = "show_game_display";
        reply.status.game_id = gameId;
        reply.status.msg = "You are now watching a Bot vs Bot match.";

        reply.status.clientId = creatorId;
        reply.recipients.add(creatorId);

        return reply;
    }

    public UserEventReply sendShowGameDisplay(int clientId) {
        UserEventReply reply = new UserEventReply();
        reply.status = new game_status();
        reply.recipients = new ArrayList<>();

        Game game = gamePageController.returnGame(clientId);

        if (game == null) {
            System.out.println("[DEBUG] No active game found for client ID " + clientId);
            return null; 
        }

        reply.status.type = "show_game_display";
        reply.status.game_id = 1; // Needs GameID from GM
        reply.status.player = "Player " + clientId; // Needs PlayerName from GM
        reply.status.clientId = clientId; 
        reply.status.player_color = "w"; // Needs PlayerColor from GM
        reply.status.starting_player = 1; // Needs StartingPlayer from GM (return w/ clientId)

        reply.recipients.add(clientId);

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
        reply.status.starting_player = 1;

        // Send to both players (hardcoded values for test)
        reply.recipients.add(1); // Luigi
        reply.recipients.add(2); // Mario

        return reply;
    }

}
