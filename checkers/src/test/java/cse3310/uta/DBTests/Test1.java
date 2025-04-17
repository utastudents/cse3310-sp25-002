package cse3310.uta.DBTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uta.cse3310.DB.DB;


public class Test1
{
    private Connection connection;
    @Before
    public void SetUp() throws SQLException
    {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        DB.createTable();
    }

    @Test
    public void testInsertUser() throws SQLException
    {


        DB.insertUser("prabin28"); // Change username every test

        List<String> leaderboard = DB.getLeaderboard();

        for( String element : leaderboard )
        {
            System.out.println(element);
        }

    }
}