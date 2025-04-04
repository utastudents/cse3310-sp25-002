package uta.cse3310.Bot.BotII;

import uta.cse3310.Bot.Bot;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;

import java.util.LinkedList;

public class BotII extends Bot {

    /* Sending Moves from Bot 1 to the GameManager */
    @Override
    protected Moves sendMove() {
        return this.moves;
    }

}
