package uta.cse3310.PageManager;

import java.util.List;
import java.util.ArrayList; // Keep original imports if needed elsewhere, though not directly in declarations here

public class game_status {

    Integer turn;
    Float score;
    public int clientId;
    public int playerId;
    public boolean isBot;
    public String player;
    public int game_id;
    public String playerName;
    public String Status;
    public String Message;
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
    public String current_move;
    public int id;
    public String msg;
    public List<Integer> capturedSquare;

}