package uta.cse3310.GameManager;

import uta.cse3310.PairUp.Match;
//import com.fasterxml.jackson.databind.ObjectMapper; (library for JSON conversion)
import java.io.IOException;

public class GamePairController{
    private PairUp pairUpSys;
    //private ObjectMapper objectMapper; (JSON conversion)

    public GamePairController(PairUp pairUpSys){
        /*this.pairUpSys = pairUpSys;
        this.objectMapper = new ObjectMapper();*/
    }

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
        } catch (IOException e) {
            System.err.println("Error initializing game from JSON: " + e.getMessage());*/
            return null;
        }
    }
}
