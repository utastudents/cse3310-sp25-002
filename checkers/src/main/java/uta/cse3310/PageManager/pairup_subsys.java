package uta.cse3310.PageManager;
import java.time.LocalDateTime;
import java.util.List;


public class pairup_subsys {

    public class reply_m
    {
        public String ActivePlayers;
        public LocalDateTime timestamp;
    }
    public class PlayerEntry
    {
        public String playerId;
        public String playerName;
        public boolean playAgainstBot;
        public LocalDateTime timestamp;
    }

    public class JSONconverter
    { 
    public static List<PlayerEntry> parsePlayersFromJson(String json) 
    {

        return List.of(); 
    }

    public static String convertRepliesToJson(List<reply_m> replies) 
    {
        
        return "{}"; 
    }
    
}
}
