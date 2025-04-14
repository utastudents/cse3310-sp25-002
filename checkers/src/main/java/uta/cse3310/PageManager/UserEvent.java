package uta.cse3310.PageManager;

import com.google.gson.Gson;

public class UserEvent {

    // System-assigned user ID
    public Integer id;

    // User account info
    public String playerName;
    public String password;

    // Game statistics
    public int win;
    public int lose;
    public int totalGames;
    public String rank;

    // Event info from frontend
    public String type;        // e.g., move, resign, draw, draw_accept, login
    public int gameId;      // Game session ID
    public int[] from;         // Move starting position
    public int[] to;           // Move target position
    public int[] square;       // For get_allowed_moves
    public String msg;         // Optional message from frontend

    // Parse JSON into UserEvent
    public static UserEvent fromJSON(String jsonStr) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, UserEvent.class);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse UserEvent JSON: " + e.getMessage());
            return null;
        }
    }

    // Optional: Convert back to JSON
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
