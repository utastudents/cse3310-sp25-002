package uta.cse3310.PageManager;

import java.util.List;
import java.util.ArrayList;

public class game_status {
    // user data
    Integer turn;
    Float score;
    public int clientId;
    public int playerId;
    public boolean isBot;
    public String player;
    public int game_id;
    public String playerName;

    // login data
    public String Status;
    public String Message;

    // game state data
    public boolean gameOver = false;
    public Integer winner = null;
    public Integer loser = null;
    public boolean draw = false;
    public String type;
    public String player_color;       
    public String starting_player;    
    public List<Integer> from;
    public List<Integer> to;
    public List<List<Integer>> legal_moves;

    // adding this even if redundant because i do not want to change the rest of the code
    // Name of the player whose turn it is next
    public String current_move;
    // ID of the player whose turn it is next
    public int id;
    //debugging msg
    public String msg;

}
