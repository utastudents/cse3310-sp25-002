package uta.cse3310.DB;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Validate
{
    public static void ValidateUser(String username)
    {   
        String [] databaseUsername = new String[10];
        // DB db = new Db();
        // String databaseUsername = db.getLeaderboard();
        for (String users : databaseUsername)
        {
            if (users == username || users == null)
            {
                return;
            }
            else
            {
                System.out.println("Invalid username");
                return;
            }
        }
    }


}