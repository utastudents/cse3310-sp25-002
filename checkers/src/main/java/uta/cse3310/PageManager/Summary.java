// This is for Work Package: Summary
//
// We will send the users username, client ID and Score.

package uta.cse3310.PageManager;

import java.util.ArrayList;
import java.util.List;

public class Summary
{
    //store the summaries of the (multiple) users to display
    private List<userSummary> allUsersSummaries;

    //constructor
    public Summary()
    {
        this.allUsersSummaries = new ArrayList<>();
    }

    //individual user summary 
    private static class userSummary
    {
        //each summary will hold this information
        String username;
        int clientID;
        int score;

        userSummary(String username, int clientID, int score)
        {
            this.username = username;
            this.clientID = clientID;
            this.score = score;
        }
    }

    //retrieve the username once given
    //public String getUsername()
    //{
        //return username;
    //}

    //retrieve the user's clientID
    //public int getClientID()
    //{
        //return clientID;
    //}

    //retrieve the user's score once game is done
    //public int getUserScore()
    //{
        //return score;
    //}

    //
    //public void addSummary(String username, int clientID, int score)
    //{
        //allUsersSummaries.add(new userSummary(username, clientID, score));
    //}

    //Summary wants a search player bar
    //public String searchBar(String searchUser)
    //{
        //JsonObject response = new JsonObject();

        //return response;
    //}
}
