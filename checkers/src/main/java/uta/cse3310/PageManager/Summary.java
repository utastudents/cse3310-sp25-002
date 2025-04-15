// This is for Work Package: Summary
//
// We will send the users username, client ID and Score.

package uta.cse3310.PageManager;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

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

    //once a player has completed their round,
    //get that information and add it so can store to display later
    public void addSummary(String username, int clientID, int score)
    {
        allUsersSummaries.add(new userSummary(username, clientID, score));
    }

    //Summary wants a search player bar
    //for the user to look at score and rank
    public String searchBar(String searchUser)
    {
        //receive the action of search
        JsonObject response = new JsonObject();

        //now need to iterate through the usernames
        for(userSummary eachUser : allUsersSummaries)
        {
            //once found the username in the search
            if(eachUser.username.equals(searchUser))
            {
                //get the information to display
                response.addProperty("Username",eachUser.username);
                response.addProperty("ClientID",eachUser.clientID);
                response.addProperty("Score",eachUser.score);
                response.addProperty("Status","Success!");
            }
        }

        //otherwise could not find anything
        response.addProperty("Status", "Nothing to find");
        response.addProperty("Message","Could not find user");

        //should return the user that is searched
        return response.toString();
    }
}
