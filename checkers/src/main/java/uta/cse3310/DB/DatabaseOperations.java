package uta.cse3310.DB;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseOperations
{
	public static void createTable(Connection Database)
	{
		string url;

		string CreateStatement; //here the schema is to be established and create statement 
		
		string stmt; // will be used to during implementation of CREATE statement for table
	}

	public static void insertUser(Connection Conn, String Username, int rank, int GamesPlayed)
	{
		string url;

		string Attributes; // will parse attributes from other interfaces

		string InsertStatement; //here the insert statement created

		string pstmt; //here we will parse through the data and set the 
	}

	public static void getLeaderboard(Connection Database)
	{
		string url;
		string selectStatement; //will be used to create the select statements 
	}

	public static void UpdatePlayer(Connection Database)
	{
		string updateStatement;
	}
}

