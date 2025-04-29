// This is for Work Package: New Account/Login
// 
// They will send us input of the usernames as a JSON string.
// We will verify if user already exists in DB before adding it.
// Page Manager will send back a message if the username already exists.
// If yes, then send back error code.
// If not, add to DB and send back success message.
// This will ensure no duplicate usernames while providing immediate feedback to users during registration.

// Would be better to return a JSON object with status and message rather than a string.
package uta.cse3310.PageManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uta.cse3310.DB.DB;
import uta.cse3310.DB.SQLiteConnector;
import uta.cse3310.App;

public class NewAcctLogin
{
    //constructor
    public NewAcctLogin()
    {
        //The usernames will be entered here so make
        //sure the table is made here
        DB.createTable();
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
