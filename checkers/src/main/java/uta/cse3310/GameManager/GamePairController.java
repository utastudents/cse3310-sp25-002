package uta.cse3310.GameManager;

import uta.cse3310.PairUp.Match;

public class GamePairController{

    public GamePairController(){
    }

    public Game newMatch(Match match){
        System.out.println("[DEBUG] GamePairController: New Match received with GameID " + match.getGameId());
        int player1ID = match.getPlayer1Id();
        int player2ID = match.getPlayer2Id();
        boolean player1Color = match.getPlayer1Color();
        boolean player2Color = match.getPlayer2Color();
        int gameNumber = match.getGameId();

        Game newGame = new Game(player1ID, player2ID, player1Color, player2Color, gameNumber);

        return newGame;
    }
}
