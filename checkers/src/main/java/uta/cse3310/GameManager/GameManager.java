package uta.cse3310.GameManager;

import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.Bot.BotI.BotI;
import uta.cse3310.Bot.BotII.BotII;
//import uta.cse3310.Controllers.GamePageController;  
//import uta.cse3310.Controllers.GamePairController;  
import java.util.ArrayList;
import java.util.List;


public class GameManager {
    GamePlay gp;
    GameTermination gt;
    BotI b1;
    BotII b2;
    List<Game> games;
    // GamePageController gamePageController;
    // GamePairController gamePairController;

    public GameManager() {
        gp = new GamePlay();
        gt = new GameTermination();
        b1 = new BotI();
        b2 = new BotII();
        games = new ArrayList<>();
        //  gamePageController = new GamePageController();
        //  gamePairController = new GamePairController();
    }

    public void addGame(Game game) {
        games.add(game);
        // gamePairController.registerGame(game);
    }

    public Game getGame(int gameNumber) {
        for (Game game : games) {
            if (game.gameNumber() == gameNumber) {
                return game;
            }
        }
        return null;
    }

    public List<Game> getGames() {
        return games;
    }

    public void sendGameToComponents(Game game) {
        // gp.processGame(game);
        // gt.evaluateGame(game);
        // b1.analyzeGame(game);
        // b2.analyzeGame(game);
        // gamePageController.updateGame(game);
    }
}
