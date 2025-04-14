package uta.cse3310.PageManager;

import com.google.gson.Gson;

public class UserEvent {
    public Integer id; // the user id that created the message

    // user information
    public String UserName;  //userName of player
    public String Password;  //password of player

    // game status
    public int win;                   // number of wins
    public int lose;                 // number of losses
    public int TotalGames;           //total games played
    public String rank;              // dont know the ranking system yet

    // Event info
    public String type;         // move, resign, draw, draw_accept, etc.
    public String gameId;       // identifies the game session
    public int[] from;          // for move: starting position
    public int[] to;            // for move: target position
    public int[] square;        // for get_allowed_moves requests

    public String msg;     


    public static UserEvent fromJSON(String jsonStr) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, UserEvent.class);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse UserEvent JSON: " + e.getMessage());
            return null;
        }
    }

}
