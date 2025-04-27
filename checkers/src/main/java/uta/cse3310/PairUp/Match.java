package uta.cse3310.PairUp;

/*
    The Match class will serve as an object that stores
    all information needed to start a match, which will
    then be sent to GameManager
*/
public class Match {

    private int player1Id; // Player 1's unique ID
    private int player2Id; // Player 2's unique ID
    private String player1Name; // Player 1's name
    private String player2Name; // Player 2's name
    private Boolean isBot; // Indicates if the match is against a bot
    private int gameId; // Unique game ID for the match
    private Boolean player1Color; // Player 1's piece color (true for one color, false for the other)
    private Boolean player2Color; // Player 2's piece color (true for one color, false for the other)
    private int creatorID = -1; // Optional, for use in bot v bot matches


    public Match(int player1Id, int player2Id, String player1Name, String player2Name,
     Boolean isBot, int gameId, Boolean player1Color, Boolean player2Color) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.isBot = isBot;
        this.gameId = gameId;
        this.player1Color = player1Color;
        this.player2Color = player2Color;
    }

    public Match(int bot1Id, int bot2Id, int gameId, int creatorID) {
        player1Id = bot1Id;
        player2Id = bot2Id;
        player1Name = "Bot " + bot1Id;
        player2Name = "Bot " + bot2Id;
        isBot = true;
        this.gameId = gameId;
        player1Color = true;
        player2Color = false;
        this.creatorID = creatorID;
    }

    /* Method to get the player1 ID from MatchMaking class
       Returns an string to be used in Match class to create JSON
    */
    public int getPlayer1Id(){
        return player1Id;
    }

    /* Method to get the player2 ID from MatchMaking class
       Returns an string to be used in Match class to create JSON 
    */
    public int getPlayer2Id(){
        return player2Id;
    }

    /* Method to get the player1 Name from MatchMaking class
       Returns an string to be used in Match class to create JSON
    */
    public String getPlayer1Name(){
        return player1Name;
    }    

    /* Method to get the player2 Name from MatchMaking class
       Returns an string to be used in Match class to create JSON
    */
    public String getPlayer2Name(){
        return player2Name;
    }

    /* Method to check if the player is playing against a bot or another player
       Returns an boolean value to be used in Match class to create JSON
    */
    public Boolean isBotGame(){
        return isBot;
    }

    /* Method to get the gameId from MatchMaking class
       Returns an string to be used in Match class to create JSON
    */
    public int getGameId(){
        return gameId;
    }

    /* Method to get the piece color of player1
       Returns an boolean to be used in Match class to create JSON
    */
    public Boolean getPlayer1Color(){
        return player1Color;
    }

    /* Method to get the piece color of player2
       Returns an boolean to be used in Match class to create JSON
    */
    public Boolean getPlayer2Color(){
        return player2Color;
    }
}
