package uta.cse3310.PairUp;


/*
    The PlayersInMatchmaking class will serve
    as the object representing a player that is
    currently looking for another player to duel.
    This class will contain all relevant information
    to a player that will allow them to be matched
    up with an appropriate opponent.
*/
public class PlayerInMatchmaking {

    private long timeOfEntry;
    private int playerID;
    private String playerName;
    private boolean playAgainstBot;
    private int wins;

    //  constructor
    public PlayerInMatchmaking(long timeOfEntry, int playerID, String playerName, boolean playAgainstBot, int wins) {
        this.timeOfEntry = timeOfEntry;
        this.playerID = playerID;
        this.playerName = playerName;
        this.playAgainstBot = playAgainstBot;
        this.wins = wins;
    }
    
    //  getters
    public long getTimeOfEntry() {
        return timeOfEntry;
    }
    
    public int getPlayerID() {
        return playerID;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public boolean isPlayAgainstBot() {
        return playAgainstBot;
    }
    
    public int getWins() {
        return wins;
    }

    public long getQueueTime() {
        return (long) (System.currentTimeMillis() - timeOfEntry);
    }

    @Override
    public String toString() {
        return this.playerName;
    }
}
