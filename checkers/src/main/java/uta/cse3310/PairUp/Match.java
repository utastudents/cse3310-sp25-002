package uta.cse3310.PairUp;

/*
    The Match class will serve as an object that stores
    all information needed to start a match, which will
    then be sent to GameManager
*/
public class Match {
    public Match() {
        
    }

    /* Method to get the player1 ID from MatchMaking class
       Returns an string to be used in Match class to create JSON
    */
    public String getPlayer1Id(){
        return "";
    }

    /* Method to get the player2 ID from MatchMaking class
       Returns an string to be used in Match class to create JSON 
    */
    public String getPlayer2Id(){
        return "";
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
}
