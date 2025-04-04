package uta.cse3310.GameManager;

import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GameTermination.GameTermination;
import uta.cse3310.Bot.BotI.BotI;
import uta.cse3310.Bot.BotII.BotII;
//import uta.cse3310.Controllers.GamePageController;  // bd9659
//import uta.cse3310.Controllers.GamePairController;  // bd9659
import java.util.ArrayList;
import java.util.List;


public class GameManager {
    GamePlay gp;
    GameTermination gt;
    BotI b1;
    BotII b2;
    List<Game> games;
    // bd9659 GamePageController gamePageController;
    // bd9659 GamePairController gamePairController;

    public GameManager() {
        gp = new GamePlay();
        gt = new GameTermination();
        b1 = new BotI();
        b2 = new BotII();
        games = new ArrayList<>();
        // bd9659  gamePageController = new GamePageController();
        // bd9659  gamePairController = new GamePairController();
    }

    public void addGame(Game game) {
        games.add(game);
        // bd9659 gamePairController.registerGame(game);
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
        // bd9659 gp.processGame(game);
        // bd9659 gt.evaluateGame(game);
        // bd9659 b1.analyzeGame(game);
        // bd9659 b2.analyzeGame(game);
        // bd9659 gamePageController.updateGame(game);
    }
}
