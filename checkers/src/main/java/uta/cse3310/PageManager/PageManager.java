package uta.cse3310.PageManager;

import java.util.ArrayList;

import uta.cse3310.DB.DB;
import uta.cse3310.PairUp.PairUp;
import uta.cse3310.PageManager.UserEvent;
import uta.cse3310.PageManager.UserEventReply;

public class PageManager {
    DB db;
    PairUp pu;
    Integer turn = 0; // just here for a demo. note it is a global, effectively and
                      // is not unique per client (or game)



    


// CODE FOR PAIR UP subsystem
    private final PairUp pairUp = new PairUp();

    public String handleUserReq(String json_UI) {
        try {
         
            List<PlayerEntry_PairUP> PlayersWaiting = JSONConverter_PairUP.parsePlayersFromJson(jsonFromClient);

            List<Match_PairUPr> PlayersActive = PairUp.pairPlayers(PlayersWaiting);

            return JSONConverter_PairUP.convertMatchesToJson(PlayersActive);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to process player pairing.\"}";
        }
    }

    // CODE FOR PAIR UP SUB system



    

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
