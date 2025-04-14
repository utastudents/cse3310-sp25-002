package uta.cse3310.PageManager;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;



public class pairup_subsys {

    private final PageManager pageManager = new PageManager();
    private final NewAcctLogin newAcctLogin;

    private int clientID = -1;

    public int extractClientId(UserEvent event) 
    {
        if (event == null) {
            System.out.println("UserEvent is null");
            return -1; 
        }

        return event.id; 
    }

    public void handleEvent(UserEvent event) {
        this.clientID = extractClientId(event);
        System.out.println("Extracted Client ID: " + clientID);
    }


    public pairup_subsys(NewAcctLogin newAcctLogin)
    {
        this.newAcctLogin = newAcctLogin;
    }



    public void JoinAndLogin(String inputJSON, Map<String, String> joinData) 
    {
        // Account Log In data
        String input = newAcctLogin.processUsernameInput(inputJSON);

        // JoinGameHandler data
        JoinGameHandler joinHandler = new JoinGameHandler();
        JoinGameHandler.Result Result = joinHandler.processJoinGame(joinData);

        // timestamp
        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.toInstant(ZoneOffset.UTC).toEpochMilli();

        // Send to PageManager
        pageManager.handleNewPlayer(
                timestamp,
                clientID,
                input,
                Result.playAgainstBot,
                0 
        );

}
}
