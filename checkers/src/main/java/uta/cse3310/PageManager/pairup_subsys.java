package uta.cse3310.PageManager;
import java.time.LocalDateTime;

public class pairup_subsys {

    public static class reply_m {
        public String ActivePlayers;
        public LocalDateTime timestamp;

        public reply_m(String activePlayers, LocalDateTime timestamp) {
            this.ActivePlayers = activePlayers;
            this.timestamp = timestamp;
        }
    }

    public static class PlayerEntry {
        public String ClientId;
        public String UserName;
        public boolean playAgainstBot;
        public LocalDateTime timestamp;

        public PlayerEntry(LocalDateTime timestamp, String ClientId, String UserName, boolean playAgainstBot) {
            this.timestamp = timestamp;
            this.ClientId = ClientId;
            this.UserName = UserName;
            this.playAgainstBot = playAgainstBot;
        }
    }

    
}
