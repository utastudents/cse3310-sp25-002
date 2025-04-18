// This is for Work Package: New Account/Login
// 
// They will send us input of the usernames as a JSON string.
// We will verify if user already exists in DB before adding it.
// Page Manager will send back a message if the username already exists.
// If yes, then send back error code.
// If not, add to DB and send back success message.
// This will ensure no duplicate usernames while providing immediate feedback to users during registration.

package uta.cse3310.PageManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uta.cse3310.DB.DB;
import uta.cse3310.DB.SQLiteConnector;

public class NewAcctLogin
{
    //constructor
    public NewAcctLogin()
    {
        //The usernames will be entered here so make
        //sure the table is made here
        DB.createTable();       
    }

    //First, lets process the input of the usernames
    public String processUsernameInput(String inputJSON)
    {
        JsonObject response = new JsonObject();

        try
        {
            //receive the username
            JsonObject input = JsonParser.parseString(inputJSON).getAsJsonObject();
            String username = input.get("username").getAsString();

            //Make sure user actually typed something
            if(username == null)
            {
                response.addProperty("Status", "Error");
                response.addProperty("Message", "Please enter username");
                return response.toString();
            }
            //Page Manager already has the main logic:
            PageManager pm = new PageManager();
            JsonObject validate = pm.handleUsernameValidation(username);
            boolean accepted = validate.get("accepted").getAsBoolean();

            response.addProperty("Status", accepted ? "Success!" : "Error");
            response.addProperty("Message", accepted ? "Success!" : "Error");
        }
        catch(Exception e)
        {
            //error handling
            System.out.println("Could not process username: " + e.getMessage());
            response.addProperty("Status", "Error");
            response.addProperty("Message","Invalid");
        }
        //return back the reponse of success or failure
        return response.toString();
    }

    //check if user already in database
    public boolean usernameExists(String username)
    {
        try
        {
            //following what DB has
            String sqlString = "SELECT username FROM USERS WHERE username = ?";
            //DB already has the SQLite Connector so just need to connect to same one
            try(java.sql.Connection connection = SQLiteConnector.connect();
                java.sql.PreparedStatement st = connection.prepareStatement(sqlString))
            {
                st.setString(1,username);
                java.sql.ResultSet rs = st.executeQuery();
                //Confirm if there is an instance
                //of that username.
                return rs.next();
            }
        }
        catch(Exception e)
        {
            //If all else fails, do error handling
            System.out.println("Could not verify if username exists: " + e.getMessage());
            
            //does user exist?
            //return true if user is found...otherwise...
            return false;
        }
    }

    // Helper to extract username from JSON
    public String extractUsernameFromInput(String inputJSON)
    {
        return JsonParser.parseString(inputJSON).getAsJsonObject().get("username").getAsString();
    }
}
