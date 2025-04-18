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
        this.timeOfEntry = timeOfEntry; // The time the player entered the matchmaking queue
        this.playerID = playerID; // The unique ID of the player
        this.playerName = playerName; // The name of the player
        this.playAgainstBot = playAgainstBot;  // Indicates whether the player wants to play against a bot
        this.wins = wins; // The number of wins the player has
    }
    
    //  getters
    /*
        Gets the time the player entered the matchmaking queue.
        Returns:
        - A long representing the time of entry in milliseconds.
    */
    public long getTimeOfEntry() {
        return timeOfEntry;
    }
    
    /*
        Gets the unique ID of the player.
        Returns:
        - An integer representing the player's ID.
    */
    public int getPlayerID() {
        return playerID;
    }
    
    /*
        Gets the name of the player.
        Returns:
        - A string representing the player's name.
    */
    public String getPlayerName() {
        return playerName;
    }
    
    /*
        Checks if the player wants to play against a bot.
        Returns:
        - A boolean: true if the player wants to play against a bot, false otherwise.
    */
    public boolean isPlayAgainstBot() {
        return playAgainstBot;
    }
    
    /*
        Gets the number of wins the player has.
        Returns:
        - An integer representing the player's number of wins.
    */
    public int getWins() {
        return wins;
    }

    /*
        Calculates the time the player has been in the matchmaking queue.
        Returns:
        - An integer representing the queue time in milliseconds.
    */
    public long getQueueTime() {
        return (long) (System.currentTimeMillis() - timeOfEntry);
    }

    /*
        Converts the PlayerInMatchmaking object to a string representation.
        Returns:
        - A string containing the player's name.
    */
    @Override
    public String toString() {
        return this.playerName;
    }
}
