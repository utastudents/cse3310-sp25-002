package uta.cse3310.PageManager;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;



public class pairup_subsys {

    private final PageManager pageManager = new PageManager();
    private final NewAcctLogin newAcctLogin;

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
                Result.clientID,
                input,
                Result.playAgainstBot,
                0 
        );

}
