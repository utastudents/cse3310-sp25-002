package uta.cse3310.PageManager;

import java.util.List;
import java.util.ArrayList;

public class game_status {
    // this is just a made up example to demonstrate data flow
    Integer turn;
    Float score;
    public Integer gameID;
    // game state flags
    public boolean gameOver = false;
    public Integer winner = null;
    public Integer loser = null;
    public String msg;
    public String type;
    public int game_id;
    public String player;
    public List<Integer> from;
    public List<Integer> to;

}
