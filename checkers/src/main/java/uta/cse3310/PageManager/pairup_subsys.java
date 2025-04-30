package uta.cse3310.PageManager;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;



public class pairup_subsys {

    private final PageManager pageManager;
    private final NewAcctLogin newAcctLogin;

    private int clientId = -1;

    public int extractclientId(UserEvent event) 
    {
        if (event == null) {
            System.out.println("UserEvent is null");
            return -1; 
        }

        return event.id; 
    }

    public void handleEvent(UserEvent event) {
        this.clientId = extractclientId(event);
        System.out.println("Extracted Client ID: " + clientId);
    }


    public pairup_subsys(PageManager pageManager, NewAcctLogin newAcctLogin)
    {
        this.pageManager = pageManager;
        this.newAcctLogin = newAcctLogin;
    }



    public void JoinAndLogin(String inputJSON, Map<String, String> joinData) 
    {
        // Account Log In data
        String username = newAcctLogin.extractUsernameFromInput(inputJSON);

        // JoinGameHandler data
        JoinGameHandler joinHandler = new JoinGameHandler();
        JoinGameHandler.Result Result = joinHandler.processJoinGame(joinData);

        // timestamp
        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.toInstant(ZoneOffset.UTC).toEpochMilli();

        // Send to PageManager
       pageManager.handleNewPlayer(
                timestamp,
                Result.clientId,
                username,
                Result.playAgainstBot,
                0 
        );
        pageManager.handlePlayerRemoval(100);

}
}
