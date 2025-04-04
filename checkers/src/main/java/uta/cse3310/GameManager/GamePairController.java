package uta.cse3310.GameManager;

import uta.cse3310.PairUp.Match;
//import com.fasterxml.jackson.databind.ObjectMapper; (library for JSON conversion)
import java.io.IOException;

public class GamePairController{
    // bd9659 private PairUp pairUpSys;

    //private ObjectMapper objectMapper; (JSON conversion)

    //bd96959 public GamePairController(PairUp pairUpSys){
        /*this.pairUpSys = pairUpSys;
    // bd9659  this.objectMapper = new ObjectMapper();*/

    // bd9659 }

    public Game initializeGame(String pairUpJson){
        /*try{
            PairUp.Match match = objectMapper.readValue(pairUpJson, PairUp.Match.class); //Parse JSON input to extract info from pairUp

            int player1ID = Integer.parseInt(match.getPlayer1Id());
            int player2ID = Integer.parseInt(match.getPlayer2Id());

	    boolean player1color = true;
            boolean player2color = false;

            int gameNumber;
            try {
                gameNumber = Integer.parseInt(match.getGameId());
            } catch(NumberFormatException e){
                System.err.println("Invalid game ID format: " + match.getGameId());
                return null;
            }

            Game newGame = new Game(player1ID, player2ID, player1color, player2color, gameNumber);

            return newGame;
        // bd9659 } catch (IOException e) {
        // bd9659    System.err.println("Error initializing game from JSON: " + e.getMessage());*/
        // bd9650    return null;
        // bd9659 }
        return null;
    }
}
