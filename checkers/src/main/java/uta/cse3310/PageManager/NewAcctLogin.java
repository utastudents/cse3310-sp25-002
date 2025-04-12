// This is for Work Package: New Account/Login
// 
// They will send us input of the usernames as a JSON string.
// We will verify if user already exists in DB before adding it.
// Page Manager will send back a message if the username already exists.
// If yes, then send back error code.
// If not, add to DB and send back success message.
// This will ensure no duplicate usernames while providing immediate feedback to users during registration.

package uta.cse3310.PageManager;

import java.sql.Connection;// database connection

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NewAcctLogin
{
    private Connection userDB;

    //constructor
    public NewAcctLogin(Connection userDB)
    {
        this.userDB = userDB;
    }

    //check if user already in database
    public boolean usernameExists(String username)
    {
        //sql database code here ?
        //String
        //try-catch

        //does user exist?
        //return true if user is found...otherwise...
        return false;
    }

    //add user to database
    public boolean addUser(String username)
    {
        if(usernameExists(username))
        {
            return false;//user exists so no need to add
        }

        //sql database code here ?
        //String
        //try-catch

        //otherwise, return true to add user
        return true;
    }

    //process the input of the usernames
    public String processUsernameInput(String inputJSON)
    {
        JsonObject response = new JsonObject();

        try
        {
            //receive the username
            JsonObject input = JsonParser.parseString(inputJSON).getAsJsonObject();
            String username = input.get("username").getAsString();

            //now check if already exists
            if(usernameExists(username))
            {
                //if it does, then return error message
                response.addProperty("Status", "Error");
                response.addProperty("Message","Username already exists");
            }
            else if(addUser(username))
            {
                //if not, then success! add the user
                response.addProperty("Status", "Success");
                response.addProperty("Message","Success!");
            }
            else
            {
                //error message for other failures
                response.addProperty("Status", "Error");
                response.addProperty("Message","Failed to add user");
            }
        }
        catch(Exception e)
        {
            //error handling
            response.addProperty("Status", "Error");
            response.addProperty("Message","Invalid");
        }
        //return back the reponse of success or failure
        return response.toString();
    }
}
