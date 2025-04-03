package uta.cse3310.PageManager;

public class UserEvent {
    public Integer id; // the user id that created the message

    // user information
    public String UserName;  //userName of player
    public String Password;  //password of player

    // game status
    public int win;                   // number of wins
    public int lose;                 // number of losses
    public int TotalGames;           //total games played
    public String rank;              // dont know the ranking system yet

    String msg;     


    public static UserEvent fromJSON(Sting jsonStr){
        // TODO: implement this method
        // This method should parse the JSON string and create a UserEvent object
    }

}
