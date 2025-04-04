// This is for Work Package: Summary
//
// We will send the users username, client ID and Score.

package uta.cse3310.PageManager;

public class Summary
{
    private String username;
    private int clientID;
    private int score;

    //constructor
    public Summary(String username, int clientID, int score)
    {
        this.username = username;
        this.clientID = clientID;
        this.score = score;
    }

    //retrieve the username once given
    public String getUsername()
    {
        return username;
    }

    //retrieve the user's clientID
    public int getClientID()
    {
        return clientID;
    }

    //retrieve the user's score once game is done
    public int getUserScore()
    {
        return score;
    }
}
