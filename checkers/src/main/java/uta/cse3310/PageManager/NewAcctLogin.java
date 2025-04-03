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

public class NewAcctLogin
{
    private Connection userDB;

    //constructor
    public NewAcctLogin(Connection userDB)
    {
        this.userDB = userDB;
    }

    //add user to database
    public boolean AddUser(String username)
    {
        if(UsernameExists(username))
        {
            return false;//user exists so no need to add
        }       
    }

    //check if user already in database
    public boolean UsernameExists(String username)
    {
        //does user exist?
        //return true if user is found...otherwise...
        return false;
    }
}
