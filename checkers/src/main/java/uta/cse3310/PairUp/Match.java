package uta.cse3310.PairUp;

/*
    The Match class will serve as an object that stores
    all information needed to start a match, which will
    then be sent to GameManager
*/
public class Match {

    private int playerId;
    private int player2Id;
    private String player1Name; 
    private String player2Name;
    private Boolean isBot;
    private int gameId;
    private Boolean player1Color;
    private Boolean player2Color;


    public Match(int player1Id, int player2Id, String player1Name, String player2Name,
     Boolean isBot, int gameId, Boolean player1Color, Boolean player2Color) {
        
    }

    /* Method to get the player1 ID from MatchMaking class
       Returns an string to be used in Match class to create JSON
    */
    public int getPlayer1Id(){
        //return player1Id;
        return 0;
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
        return "";
    }    

    /* Method to get the player2 Name from MatchMaking class
       Returns an string to be used in Match class to create JSON
    */
    public String getPlayer2Name(){
        return "";
    }

    /* Method to check if the player is playing against a bot or another player
       Returns an boolean value to be used in Match class to create JSON
    */
    public Boolean isBotGame(){
        return true;
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
        return true;
    }

    /* Method to get the piece color of player2
       Returns an boolean to be used in Match class to create JSON
    */
    public Boolean getPlayer2Color(){
        return false;
    }
}
