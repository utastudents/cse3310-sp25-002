package cse3310.uta.DBTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uta.cse3310.DB.DB;
import uta.cse3310.DB.GetRank;
import uta.cse3310.DB.Validate;

public class Test2 {
    
    private Connection connection;

    @Before
    public void SetUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DB.createTable();
    }


    @Test
    public void testGetRank() {
        int rank = GetRank.getRank("prabin28");
        System.out.println("Rank: " + rank);
    }

    @Test
    public void testValidateUser() {
        Validate.ValidateUser("prabin28");
    }
}
