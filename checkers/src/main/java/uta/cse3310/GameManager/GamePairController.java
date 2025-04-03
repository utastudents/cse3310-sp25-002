package uta.cse3310.GameManager;

import uta.cse3310.PairUp.PairUp;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class GamePairController{
    private PairUp pairUpSys;
    private ObjectMapper objectMapper; //JSON conversion

    public GamePairController(PairUp pairUpSys){
        this.pairUpSys = pairUpSys;
        this.objectMapper = new ObjectMapper();
    }

    public Game initializeGame(String pairUpJson){
        try{
            PairUp.Match match = objectMapper.readValue(pairUpJson, PairUp.Match.class); //Parse JSON input to extract info from pairUp

            int player1ID = Integer.parseInt(match.getPlayer1Id());
            int player2ID = Integer.parseInt(match.getPlayer2Id());

            String gameID = match.getGameId();
            Game newGame = new Game(player1ID, player2ID, gameID);

            return newGame;
        }
        catch(IOException e){
            System.err.println("Error initializing game from JSON: " + e.getMessage());
            return null;
        }
    }
}
